package com.bizruntime.spark

import org.apache.log4j.Logger

import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.SparkConf
import java.util.ArrayList
import scala.collection.immutable.HashMap


/*
 *  Recommending Mutual Friend 
 * 
 * 
*/

object FriendRecommendation {
  def main(args: Array[String]): Unit = {
    new FriendRecommendation().submitJob(args)
  }
}

class FriendRecommendation extends Serializable {
  val logger = Logger.getLogger(classOf[FriendRecommendation])
  def submitJob(args: Array[String]) {

  if (args.length < 1) {
      logger.error("Usage: FriendRecommendation <input-path>")
      System.exit(1)
  }

    val friendsInputPath = args(0)
    var conf = new SparkConf()

    val ctx = new SparkContext("local[2]", "RecommendationApp", conf)

 /*    input (key:friends) as 1:2,3,4,5,6,7
  *    
  *    Make user and friends pair 
  *    map(1,)
 */   val records = ctx.textFile(friendsInputPath, 1)
      .map { str =>
        {
          val token = str.split(":")
          (token(0).toLong, token(1))
        }
      };

    // Debug 0
    records.collect().foreach { str => logger.debug(str) }

    val pairs = records.flatMap {
      case (person, frnds) => {
        val friends = frnds.split(",").map { str => str.toLong }
        var mapperOutput = friends.map { friend =>
          (person, (friend, -1L))
        }.toList

        for (i <- 0 to friends.size - 1; j <- i + 1 to friends.size - 1) yield {
          mapperOutput = mapperOutput :+ (friends(i), (friends(j), person))
          mapperOutput = mapperOutput :+ (friends(j), (friends(i), person))
        }
        mapperOutput
      }
    }

    // Debug 1
    pairs.collect().foreach(tuple => logger.debug(tuple._1 + " -> " + tuple._2))

    val grouped = pairs.groupByKey()

    // Debug 2
    grouped.collect().foreach(tuple => logger.debug(tuple._1 + " -> " + tuple._2))

    val recommendations = grouped.mapValues { values =>
      {
        var mutualFriends = new HashMap[Long, List[Long]]

        for (t2 <- values) {
          val toUser = t2._1
          val mutualFriend = t2._2
          val alreadyFriend = (mutualFriend == -1)

          if (mutualFriends.contains(toUser)) {
            if (alreadyFriend) mutualFriends += (toUser -> null)
            else if (mutualFriends.get(toUser).get != null) {
              mutualFriends += (toUser -> (mutualFriends.get(toUser).get :+ mutualFriend))
            }
          } else if (alreadyFriend) mutualFriends += (toUser -> null)
          else mutualFriends += (toUser -> List(mutualFriend))
        }
        mutualFriends.filter{case (k,v)=>v!=null}
      }
    }
    recommendations.collect().foreach(tuple => logger.debug("user1 " + tuple._1 + " & user2 -> mutual friends : " + tuple._2))

  }

}
