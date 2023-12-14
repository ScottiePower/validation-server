package com.example.demo

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.time.Instant

@RestController
class DemoController {

    private val logger: Logger = LoggerFactory.getLogger(DemoController::class.java)

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/validate"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun doValidation(@RequestBody validationRequest: String): ResponseEntity<String> {
        var validationResult = "Could not validate request."
        val sidecarUrl = "http://localhost:8071/validate"
        val validationData = "${Instant.now()} - Data to be validated by sidecar application: $validationRequest"
        logger.info("Validation request - sidecar URL: $sidecarUrl")
        val restTemplate = RestTemplate()
        val request = HttpEntity(validationData)
        try {
            val response = restTemplate
                .exchange(sidecarUrl, HttpMethod.POST, request, String::class.java)
            logger.info("response StatusCode: ${response.getStatusCode()}")
            if (!response.statusCode.isError && response.body != null) {
                validationResult = response.body!!
            }
        } catch (ex: Exception) {
            validationResult = "Exception occurred calling sidecar application. " + ex.localizedMessage
            logger.error(validationResult)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(validationResult)
    }
}