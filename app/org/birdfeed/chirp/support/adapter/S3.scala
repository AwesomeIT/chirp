package org.birdfeed.chirp.support.adapter

import awscala.s3._
import com.amazonaws.services.s3.model.ObjectMetadata

case class S3MissingConfiguration(message: String) extends Exception(message)

trait S3 {
  implicit val s3 = S3()
  implicit val bucket = s3.bucket(sys.env("AWS_S3_BUCKET_NAME")) match {
    case Some(b) => b
    case None => throw S3MissingConfiguration("Did you set AWS_S3_BUCKET_NAME to an existing bucket?")
  }

  /**
    * Put a raw file to S3
    * @param name Key in bucket
    * @param data Byte array of file
    * @return PutObjectResult
    */
  def put(name: String, data: Array[Byte]): PutObjectResult = {
    bucket.putObject(name, data, new ObjectMetadata)
  }
}
