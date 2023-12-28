package com.example.demo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.availability.ApplicationAvailability
import org.springframework.boot.availability.AvailabilityChangeEvent
import org.springframework.boot.availability.LivenessState
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.time.Instant


@RestController
class DemoController {

    private val logger: Logger = LoggerFactory.getLogger(DemoController::class.java)

    @Autowired
    private val applicationAvailability: ApplicationAvailability? = null

    @Autowired
    private val applicationContext: ApplicationContext? = null

    @Value("\${SIDECAR_URI:http://localhost:8071/validate}") private val SIDECAR_URI: String? = null

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/spring/test"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun testPage(): ResponseEntity<String> {
        logger.info("Test Page request")
        return ResponseEntity.status(HttpStatus.OK).body("${Instant.now()} Test Page. " +
                "LivenessState: ${applicationAvailability?.livenessState}, ReadinessState: ${applicationAvailability?.readinessState}. " +
                "Sidecar URI: $SIDECAR_URI")
    }

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/spring/validate"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun doValidation(@RequestBody validationRequest: String): ResponseEntity<String> {
        var validationResult: String
        logger.info("Validation request - POST body: $validationRequest")
        val restTemplate = RestTemplate()
        try {
            logger
            val response =
                restTemplate.postForEntity(SIDECAR_URI!!, validationRequest, ValidationResponse::class.java)

            if (response.statusCode.isSameCodeAs(HttpStatus.NOT_FOUND)){
                AvailabilityChangeEvent.publish(applicationContext, LivenessState.BROKEN);
            }

            if (!response.statusCode.isError && response.body != null) {
                val isRegoValid = response.body!!.valid
                val validationErrors = response.body!!.errors
                validationResult = "isRegoValid: ${isRegoValid} validationErrors: ${validationErrors}"
            } else {
                val expectedResponse = "Sidecar unexpected response status code: ${response.statusCode}"
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(expectedResponse)
            }
        } catch (ex: RestClientException) {
            validationResult = "Exception occurred calling sidecar application. ${ex.localizedMessage}"
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(validationResult)
        }
        return ResponseEntity.status(HttpStatus.OK).body(validationResult)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ValidationResponse(
        val valid: Boolean,
        val errors: String,
    )
}