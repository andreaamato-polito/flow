package it.polito.wa2.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.reactive.function.client.exchangeToFlow

@RestController
class MainController(val userRepository: UserRepository) {

    private val webClient = WebClient.create("http://localhost:8080")

    @GetMapping("/stream", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun stream(): Flow<Data> {
        return flowOf("a", "b", "c", "d")
            .map { it -> Data(it, 1.23f) }
            .onEach { delay(3000) }
    }

    @GetMapping("/process", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun process(): List<Data> {
        return webClient
            .get()
            .uri("/stream")
            .accept(MediaType.APPLICATION_NDJSON)
            .exchangeToFlow {
                response ->
                if (response.statusCode() == HttpStatus.OK)
                    response.bodyToFlow(Data::class)
                        .map { d -> Data(d.name+"1", d.price*2.0f) }
                else
                    emptyFlow()
            }
            .toList()
    }


    @GetMapping("/users", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun users(): Flow<User>{
        return userRepository.findAll()
    }

    @GetMapping("/user_names")
    suspend fun userNames(): List<String> {
        return userRepository
            .findAll()
            .map { u -> u.name }
            .toList()
    }
}

data class Data(val name: String, val price: Float)