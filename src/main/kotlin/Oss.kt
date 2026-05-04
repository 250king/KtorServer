package com.king250

import com.aliyun.sdk.service.oss2.OSSAsyncClient
import com.aliyun.sdk.service.oss2.credentials.StaticCredentialsProvider
import com.aliyun.sdk.service.oss2.models.ListObjectsV2Request
import com.aliyun.sdk.service.oss2.models.ListObjectsV2Result
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.minutes

private val mobile = AtomicReference<List<String>>(emptyList())

private val desktop = AtomicReference<List<String>>(emptyList())

suspend fun fetch(client: OSSAsyncClient, bucket: String, prefix: String): ListObjectsV2Result {
    return client.listObjectsV2Async(
        ListObjectsV2Request.newBuilder()
            .bucket(bucket)
            .prefix("background/${prefix}")
            .build()
    ).await()
}

fun Application.startOssScheduler() {
    val config = environment.config
    val cname = config.property("oss.cname").getString()
    val region = config.property("oss.region").getString()
    val bucket = config.property("oss.bucket").getString()
    val accessKeyId = config.property("oss.accessKeyId").getString()
    val accessKeySecret = config.property("oss.accessKeySecret").getString()
    val provider = StaticCredentialsProvider(accessKeyId, accessKeySecret)
    val client = OSSAsyncClient.newBuilder()
        .region(region)
        .credentialsProvider(provider)
        .build()
    launch(Dispatchers.IO) {
        while (isActive) {
            mobile.set(
                fetch(client, bucket, "mobile").contents().filterNot { it.key() == "background/mobile/" }.map {
                    "$cname/${it.key()}"
                }
            )
            desktop.set(
                fetch(client, bucket, "desktop").contents().filterNot { it.key() == "background/desktop/" }.map {
                    "$cname/${it.key()}"
                }
            )
            delay(1.minutes)
        }
    }
}

fun getRandomImage(prefix: String): String? {
    val current = if (prefix == "desktop") desktop.get() else mobile.get()
    return if (current.isEmpty()) null else current.random()
}
