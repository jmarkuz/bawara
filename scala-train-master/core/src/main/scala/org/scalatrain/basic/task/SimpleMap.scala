package org.scalatrain.basic.task

import java.util

class SimpleMap[K, V] extends PartialFunction[K, V] {

  val javaMap = new util.HashMap[K, V]()

  def size: Int = {
    javaMap.size()
  }

  def update(key: K, value: V) = {
    javaMap.put(key, value)
  }

  def apply(key: K) = {
    javaMap.get(key)
  }

  def -=(key: K) = {
    javaMap.remove(key)
  }


  def filterKey(p: K => Boolean): SimpleMap[K, V] = {
    val result = new SimpleMap[K, V]
    val it = javaMap.entrySet().iterator();
    while (it.hasNext) {
      val entry = it.next()
      if (p(entry.getKey)) {
        result(entry.getKey) = entry.getValue
      }
    }
    result
  }

  def filter(p: ((K, V)) => Boolean): SimpleMap[K, V] = {
    val result = new SimpleMap[K, V]
    val it = javaMap.entrySet().iterator();
    while (it.hasNext) {
      val entry = it.next()
      if (p(entry.getKey -> entry.getValue)) {
        result(entry.getKey) = entry.getValue
      }
    }
    result
  }

  def map[K1, V1](p: ((K, V)) => (K1, V1)): SimpleMap[K1, V1] = {
    val result = new SimpleMap[K1, V1]
    val it = javaMap.entrySet().iterator();
    while (it.hasNext) {
      val entry = it.next()
      val (k, v) = p(entry.getKey -> entry.getValue)
      result(k) = v
    }
    result
  }

  def flatMap[K1, V1](f: ((K, V)) => SimpleMap[K1, V1]): SimpleMap[K1, V1] = {
    val result = new SimpleMap[K1, V1]
    val it = javaMap.entrySet().iterator();
    while (it.hasNext) {
      val entry = it.next()
      val sm = f(entry.getKey -> entry.getValue)
      result.javaMap.putAll(sm.javaMap)
    }
    result
  }

  def foldLeft[A](zero: A)(acc: (A, (K, V)) => A): A = {
    var result = zero
    val it = javaMap.entrySet().iterator();
    while (it.hasNext) {
      val entry = it.next()
      result = acc(result, entry.getKey -> entry.getValue);
    }
    result
  }

  def collect(pf: PartialFunction[(K, V), (K, V)]): SimpleMap[K, V] = {
    val result = new SimpleMap[K, V]
    val it = javaMap.entrySet().iterator();
    while (it.hasNext) {
      val entry = it.next()
      val tuple = (entry.getKey, entry.getValue)
      if (pf.isDefinedAt(tuple)) {
        val (k, v) = pf(tuple)
        result(k) = v
      }
    }
    result
  }

  def get(k: K): Option[V] = {
    Option.apply(javaMap.get(k))
  }

  override def isDefinedAt(x: K): Boolean = javaMap.containsKey("")
}

object SimpleMap {
  def apply[K, V](args: (K, V)*): SimpleMap[K, V] = {
    val result = new SimpleMap[K, V]
    args.foreach {
      case (k, v) => result(k) = v
    }
    result
  }
}