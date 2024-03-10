/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import com.google.samples.apps.nowinandroid.core.database.model.TopicFtsEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class TopicFtsDaoTest {

    private lateinit var topicFtsDao: TopicFtsDao
    private lateinit var db: NiaDatabase

    private val topicFtsEntities = listOf(
        testTopicFtsEntity("0"),
        testTopicFtsEntity("1"),
        testTopicFtsEntity("2"),
        testTopicFtsEntity("3"),
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NiaDatabase::class.java,
        ).build()
        topicFtsDao = db.topicFtsDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun whenInsertOnce_InsertedTopicsFtsEntityOnce() = runTest {
        insertAllNewsResourceFtsEntities()
        assertEquals(4, topicFtsDao.getCount().first())
    }

    @Test
    fun whenInsertTwice_InsertedNewsResourceEntityTwice() = runTest {
        repeat(2) {
            topicFtsDao.insertAll(topics = topicFtsEntities)
        }
        assertEquals(8, topicFtsDao.getCount().first())
    }

    @Test
    fun whenDeleteAndInsertAllThreeTimes_InsertedOnce() = runTest {
        repeat(3) {
            topicFtsDao.deleteAllAndInsertAll(topics = topicFtsEntities)
        }
        assertEquals(4, topicFtsDao.getCount().first())
    }

    private suspend fun insertAllNewsResourceFtsEntities() =
        topicFtsDao.insertAll(topics = topicFtsEntities)

    private fun testTopicFtsEntity(
        number: String,
    ) = TopicFtsEntity(
        topicId = number,
        name = "name$number",
        shortDescription = "Short description$number",
        longDescription = "Long description$number",
    )
}
