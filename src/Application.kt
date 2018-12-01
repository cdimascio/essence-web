package io.github.cdimascio

import com.fasterxml.jackson.databind.SerializationFeature
import io.github.cdimascio.essence.Essence
import io.github.cdimascio.japierrors.ApiError.badRequest
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.http.content.staticBasePackage
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main(args: Array<String>): Unit {
    embeddedServer(
        Netty,
        host = "localhost",
        watchPaths = listOf("solutions/exercise4"),
        port = 8080,
        module = Application::module
    ).apply { start(wait = true) }
}


//@Suppress("unused")
//@kotlin.jvm.JvmOverloads
fun Application.module() {
    val client = HttpClient(OkHttp)

    install(Locations) {
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost()
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        static("") {
            this.staticBasePackage = "static"
            resources("/")
            defaultResource("index.html")
        }

        install(StatusPages) {}
        post("/extract") {
            val contentType = call.request.headers[HttpHeaders.ContentType] ?: ""
            if (contentType != ContentType.Text.Html.toString()) throw badRequest("content-type text/html required")
            val body = call.receiveText()

            if (body.isBlank()) throw badRequest("html required")

            val extracted = Essence.extract(body)

            call.respond(extracted)
        }
        get("/extract") {
            val url = call.request.queryParameters["url"] ?: throw badRequest("url query parameter required.")

            val html = client.get<String>(url)

            val extracted = Essence.extract(html)

            call.respond(extracted)
        }
    }
}

