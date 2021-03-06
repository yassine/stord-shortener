package com.github.yassine.shortener.dao

import org.mockito.Mockito
import org.rocksdb.RocksDB
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isNull
import strikt.assertions.isTrue


object RocksDbUrlDaoTest: Spek({

  describe("when a collision is detected") {
    it("a longer hash key should be used") {
      val rockDB = Mockito.mock(RocksDB::class.java)
      var callIndex = 0;
      Mockito.`when`(rockDB.get(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenAnswer {
        if (callIndex == 0) {
          callIndex++;
          return@thenAnswer ByteArray(15);
        } else {
          return@thenAnswer null
        }
      }
      val urlDao = RocksDbUrlDao(rockDB)
      val hash = urlDao.store("hello".toByteArray())
      expectThat(hash?.size == MIN_HASH_LENGTH + 1).isTrue()
    }
  }

  describe("when all possible key length fails") {
    it("I should get null as a hash") {
      val rockDB = Mockito.mock(RocksDB::class.java)
      Mockito.`when`(rockDB.get(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(ByteArray(15))
      val urlDao = RocksDbUrlDao(rockDB)
      val hash = urlDao.store("hello".toByteArray())
      expectThat(hash).isNull()
    }
  }

})
