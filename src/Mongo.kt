package io.github.cdimascio

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings

import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import io.netty.channel.nio.NioEventLoopGroup


internal object Mongo {
    private val url = dotenv["MONGO_URL"] ?: "missing-url"
    val client: MongoClient // = MongoClients.create(url)

    init {
        val eventLoopGroup = NioEventLoopGroup()
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(this.url))
            .applyToSslSettings {
                it.invalidHostNameAllowed(true)
                it.enabled(true)
            }
            .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
            .build()
        client = MongoClients.create(settings)
    }
}