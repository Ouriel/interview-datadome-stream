import java.net.URLEncoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import scala.util.matching.Regex

case class DataDomeFormData(
  Key: String,
  RequestModuleName: String,
  ModuleVersion: String,
  ServerName: String,
  IP: String,
  Port: Int,
  TimeRequest: Long,
  Protocol: String,
  Method: String,
  ServerHostname: String,
  Request: String,
  HeadersList: List[String],
  Host: String,
  UserAgent: String,
  Referer: String
)
{
  override def toString:String ={
    URLEncoder.encode(s"""Key=$Key&RequestModuleName=$RequestModuleName&ModuleVersion=$ModuleVersion&ServerName=$ServerName&
       |IP=$IP&Port=$Port&TimeRequest=$TimeRequest&Protocol=$Protocol&Method=$Method&ServerHostname=$ServerHostname&
       |Request=$Request&HeadersList=${HeadersList.mkString(",")}&Host=$Host&
       |UserAgent=$UserAgent&Referer=$Referer""".stripMargin, "UTF-8")
  }
}

object DataDomeFormData {

  val formater: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ssZ")
  val apacheLogRegex: Regex ="""(.*) (.*) (.*) \[(.*) (.*)\] "(.*) (.*) (.*)/(.*)" (.*) (.*) "(.*)" "(.*)" "(.*)"""".r

  def apply(log: String): DataDomeFormData =
  {
    apacheLogRegex.findFirstMatchIn(log) match {
      case Some(m) =>
        DataDomeFormData("KEY","ModuleName","1.0","ServerName",m.group(1),80,
          ZonedDateTime.parse(m.group(4)+m.group(5), formater).toInstant.toEpochMilli,m.group(8),m.group(6),"ServerHostName", m.group(7),
          List("Host","UserAgent","Referer"),m.group(14),m.group(13),m.group(12))
      case _ =>
        DataDomeFormData("ERROR","","",""
          ,"",-1,-1,"","","","",
          List(),"","","")
    }

  }
}

