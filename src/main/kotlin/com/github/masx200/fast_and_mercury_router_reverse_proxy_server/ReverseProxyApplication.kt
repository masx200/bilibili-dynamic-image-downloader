package com.github.masx200.fast_and_mercury_router_reverse_proxy_server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.ktor.client.HttpClient
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.compression.compress
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.origin
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveChannel
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.util.filter
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.toByteArray
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.mozilla.universalchardet.UniversalDetector

/**
 * The main entry point of the application. This application starts a webserver at port 8080 based on Netty.
 * It intercepts all the requests, reverse-proxying them to 上游服务器.
 *
 * In the case of HTML it is completely loaded in memory and preprocessed to change URLs to our own local domain.
 * In the case of other files, the file is streamed from the HTTP client to the HTTP server response.
 */
fun main(args: Array<String>) {

    HelloCommand(name = "fast-and-mercury-router-reverse-proxy-server") {
        println(it)
        val server = embeddedServer(CIO, port = it.port.toInt(), module = createApp(it.upstream))
        // Starts the server and waits for the engine to stop and exits.
        server.start(wait = true)
    }.main(args)
    // Creates a Netty server

}

/**
 * 创建一个命令行接口命令类，用于处理特定的命令行交互。
 * 该类继承自CliktCommand，利用Clikt库简化命令行接口的创建过程。
 * 它允许通过名称初始化命令，并定义如何处理命令行参数和选项。
 *
 * @param name 命令的名称，用于在命令行中标识该命令。
 * @param callback 当命令执行时调用的回调函数，它接收一个HelloCommand实例作为参数。
 */
class HelloCommand(name: String, val callback: (options: HelloCommand) -> Unit) : CliktCommand(name = name) {
    /**
     * 执行命令的操作。
     * 当该命令被调用时，此方法会被执行，它将调用初始化时提供的回调函数，
     * 并将当前命令实例作为参数传递给回调函数。
     */
    override fun run() {
        callback(this)
    }

    /**
     * 重写toString方法，提供命令的字符串表示。
     * 这对于调试和日志记录特别有用，因为它包含了命令的关键信息，
     * 比如upstream和port，以及基类CliktCommand的信息。
     *
     * @return 返回包含命令关键信息的字符串。
     */
    override fun toString(): String {
        return "HelloCommand(upstream='$upstream', port='$port')" + super.toString()
    }

    /**
     * 定义upstream属性，通过命令行选项参数获取其值。
     * 该属性是必须的，用户在命令行中使用-u或--upstream选项来设置该值。
     * 它代表了命令需要连接的上游服务器地址。
     */
    val upstream: String by option("-u", "--upstream", help = "upstream").required()

    /**
     * 定义port属性，通过命令行选项参数获取其值。
     * 该属性是必须的，用户在命令行中使用-p或--port选项来设置该值。
     * 它代表了命令需要连接的端口号。
     */
    val port: String by option("-p", "--port", help = "port").required()
}


const val scriptcontent = """
try {
  Object.defineProperty(window, "pageRedirect", {
    value: function () {},
  });
} catch (e) {
  console.log(e);
}

"""

fun createApp(upstream: String): Application.() -> Unit {

    return {
        install(Compression) {
            gzip()
        }
// Creates a new HttpClient
        val client = HttpClient {
            install(ContentEncoding) {
                gzip()

            }
        }
//    val 上游服务器Lang = "en"

        // Let's intercept all the requests at the [ApplicationCallPipeline.Call] phase.
        intercept(ApplicationCallPipeline.Call) {
            val originalUrl = upstream.toHttpUrl()


            // 移除路径部分
            val newUrl = HttpUrl.Builder()
                .scheme(originalUrl.scheme)
                .host(originalUrl.host)
                .port(originalUrl.port) // 如果原 URL 没有指定端口，则可以省略这一步
                .build();

            val targetUrl = newUrl.toString() + call.request.uri.slice(1..call.request.uri.length - 1)
            println(targetUrl)
            // We create a GET request to the 上游服务器 domain and return the call (with the request and the unprocessed response).
            val originalRequestBody = call.receiveChannel().toByteArray()

//            val request = HttpRequestBuilder().apply {
//                method = call.request.httpMethod
//                url(targetUrl)
//                headers.appendAll(call.request.headers)
//                headers["host"] = URL(targetUrl).host
//                setBody(originalRequestBody)
//            }
//            println(call.request.httpMethod)

//            println(call.request.host())
            println(call.request.origin)
//            println(call.request.uri)
            val headersBuilder = HeadersBuilder()
            headersBuilder.appendAll(call.request.headers)
            val messageheaders = headersBuilder.build()
            println(messageheaders)

            val toHttpUrltarget = (targetUrl).toHttpUrl()
            val response = client.request(targetUrl) {
                method = call.request.httpMethod
//
//                headers.appendAll()
                headers.clear()
//                for (header in message){}
                messageheaders.forEach { key, values ->
                    values.forEach { value ->
//                        println("$key: $value")
                        headers.append(key, value)
                    }
                }
                toHttpUrltarget.let { it1 -> headers["host"] = it1.host }
                headers["Accept-Encoding"] = "gzip"
//                println( headers["host"])
                compress("gzip")
                setBody(originalRequestBody)
            }
//            val response = client.request(upstream + call.request.uri) {
//                method = call.request.httpMethod
//                headers.appendAll(call.request.headers)
////                body=call.request.body
//            }
//        val response = client.request("https://$上游服务器Lang.上游服务器.org${call.request.uri}")

            // Get the relevant headers of the client response.
            val proxiedHeaders = response.headers
//        val location = proxiedHeaders[HttpHeaders.Location]
            val contentType = proxiedHeaders[HttpHeaders.ContentType]
            val contentLength = proxiedHeaders[HttpHeaders.ContentLength]
            println(response.status)
            println(proxiedHeaders)
            // Extension method to process all the served HTML documents
//        fun String.strip上游服务器Domain() = this.replace(Regex("(https?:)?//\\w+\\.上游服务器\\.org"), "")

            // Propagates location header, removing the 上游服务器 domain from it
//        if (location != null) {
//            call.response.header(HttpHeaders.Location, location.strip上游服务器Domain())
//        }

            // Depending on the ContentType, we process the request one way or another.
            when {
                // In the case of HTML we download the whole content and process it as a string replacing
                // 上游服务器 links.
                response.status == HttpStatusCode.OK && contentType?.startsWith("text/html") == true -> {
                    val textarray = response.bodyAsBytes()
                    val charset = detectCharset(textarray)
                    println("Detected charset: $charset")

                    // Decode the byte array using the detected charset
                    val decodedString = decodeString(textarray, charset)
//                    println("Decoded string: $decodedString")
//                    println(textarray)
                    val insertscriptintohtmlhead = { insertscriptintohtmlhead(decodedString, scriptcontent) }

                    val filteredText = if (decodedString.contains("</head>") && decodedString.contains("</html>")

                        &&
                        decodedString.contains("<head") && decodedString.contains("<html")

                    ) insertscriptintohtmlhead()
                    else decodedString
                    //.strip上游服务器Domain()

//                    println(filteredText)
                    call.respond(
                        TextContent(
                            filteredText,
                            ContentType.Text.Html.withCharset(Charsets.UTF_8),
                            response.status
                        )
                    )
                }

                else -> {
                    // In the case of other content, we simply pipe it. We return a [OutgoingContent.WriteChannelContent]
                    // propagating the contentLength, the contentType and other headers, and simply we copy
                    // the ByteReadChannel from the HTTP client response, to the HTTP server ByteWriteChannel response.
                    call.respond(object : OutgoingContent.WriteChannelContent() {
                        override val contentLength: Long? = contentLength?.toLong()
                        override val contentType: ContentType? = contentType?.let { ContentType.parse(it) }
                        override val headers: Headers = Headers.build {
                            appendAll(proxiedHeaders.filter { key, _ ->
                                !key.equals(
                                    HttpHeaders.ContentType,
                                    ignoreCase = true
                                ) && !key.equals(HttpHeaders.ContentLength, ignoreCase = true)
                            })
                        }
                        override val status: HttpStatusCode = response.status
                        override suspend fun writeTo(channel: ByteWriteChannel) {
                            response.bodyAsChannel().copyAndClose(channel)
                        }
                    })
                }
            }
        }
    }
}

/**
 * 将脚本插入到HTML头部
 *
 * 此函数的目的是将给定的脚本文本插入到给定HTML文本的头部中，以便在网页加载时首先执行该脚本
 * 它使用Jsoup库来解析HTML并操作DOM，以确保脚本正确地插入到头部
 *
 * @param htmltext 包含HTML内容的字符串，这是原始的HTML文本
 * @param scripttext 需要插入到HTML头部的脚本内容
 * @return 返回修改后的HTML字符串，其中包含了插入头部的脚本
 */
fun insertscriptintohtmlhead(htmltext: String, scripttext: String): String {
    val doc: Document = Jsoup.parse(htmltext, "", Parser.xmlParser())
    val head = doc.head()
    val script = doc.createElement("script")
    script.attr("type", "text/javascript")
    script.text(scripttext)
    head.prependChild(script)

    return (doc.html())
}
/**
 * Detects the character encoding of a byte array.
 * Uses the UniversalDetector tool to detect the character encoding of a given byte array.
 *
 * @param bytes The byte array whose character encoding is to be detected.
 * @return Returns the detected character encoding, or null if detection fails.
 */
fun detectCharset(bytes: ByteArray): String? {
    val detector = UniversalDetector(null)

    // Feed the bytes to the detector
    detector.handleData(bytes, 0, bytes.size)
    detector.dataEnd()

    // Get the detected encoding
    val encoding = detector.detectedCharset
    detector.reset()

    return encoding
}
/**
 * Decodes a byte array into a string using the specified character set.
 *
 * This function aims to convert a given byte array into a string. If a character set is provided and is not empty,
 * it will attempt to decode using the provided character set; otherwise, it will default to using UTF-8 encoding for decoding.
 *
 * @param bytes The byte array to be decoded, representing the encoded string data.
 * @param charset The character set to use for decoding, which may be null or empty.
 * @return The decoded string, or the string obtained using the default UTF-8 encoding if no character set is provided.
 */
fun decodeString(bytes: ByteArray, charset: String?): String {
    return if (charset != null && charset.isNotEmpty()) {
        String(bytes, charset(charset))
    } else {
        // If no encoding is detected, fall back to UTF-8
        String(bytes, Charsets.UTF_8)
    }
}