package com.bizruntime.topn.yarn

object TopNApp {
  
  def main(args: Array[String]): Unit = {
    new TopNDriver().submitJob(args)
  }
}