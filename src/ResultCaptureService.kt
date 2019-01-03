package io.github.cdimascio

import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.Success
import io.github.cdimascio.essence.EssenceResult
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider

data class ExtractionResult(
    val url: String? = null,
    val extraction_result: EssenceResult,
    val content: String
)
object ResultCaptureService {
    private val mongo = Mongo.client
    private val extractions = mongo.getDatabase("essence").getCollection("extractions") //, ExtractionResult::class.java)

//    init {
//        val pojoCodecRegistry = fromRegistries(
//            MongoClients.getDefaultCodecRegistry(),
//            fromProviders(PojoCodecProvider.builder().automatic(true).build())
//        )
//        extractions.withCodecRegistry(pojoCodecRegistry)
//    }

    suspend fun capture(extractionResult: ExtractionResult): Success {
        // TODO use a codecprovider
        val doc = Document()
        val extractedResult = Document()
        val metadata = extractionResult.extraction_result
        extractionResult.url?.let {
            doc.append("url", it)
        }
        doc.append("extracted_result", extractedResult)
        doc.append("content", extractionResult.content)

        extractedResult.apply {
            append("canonical_link", metadata.canonicalLink)
            append("title", metadata.title)
            append("soft_title", metadata.softTitle)
            append("description", metadata.description)
            append("authors", metadata.authors)
            append("publisher", metadata.publisher)
            append("image", metadata.image)
            append("tags", metadata.tags)
            append("keywords", metadata.keywords)
            append("links", metadata.links.map {
                Document()
                    .append("href", it.href)
                    .append("text", it.text)
            })
            append("language", metadata.language)
            append("favicon", metadata.favicon)
            append("copyright", metadata.copyright)
            append("text", metadata.text)
            append("date", metadata.date)
        }
        return extractions.insertOne(doc).awaitFirst()
    }
}