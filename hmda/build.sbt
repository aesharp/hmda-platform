import com.typesafe.sbt.packager.docker._

version := "latest"

packageName in Docker := "hmda-platform"

dockerExposedPorts := Vector(8080, 8081, 8082, 19999, 9080, 1099

dockerEntrypoint ++= Seq(
  """-Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")"""",
  """-Dakka.management.http.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")""""
)

dockerCommands :=
  dockerCommands.value.flatMap {
    case ExecCmd("ENTRYPOINT", args @ _*) =>
      Seq(Cmd("ENTRYPOINT", args.mkString(" ")))
    case v => Seq(v)
  }

javaOptions in Universal ++= Seq(
  "-J-XX:+UnlockExperimentalVMOptions",
  "-J-XX:+UseCGroupMemoryLimitForHeap",
  "-J-XX:+PrintGCDetails",
  "-J-XX:+PrintGCDateStamps",
  "-J-Xloggc:/opt/docker/gc.log",
  "-J-XX:+HeapDumpOnOutOfMemoryError",
  "-J-XX:HeapDumpPath=/dumps/oom.bin",
  "-Dcom.sun.management.jmxremote",
  "-Dcom.sun.management.jmxremote.authenticate=false",
  "-Dcom.sun.management.jmxremote.ssl=false",
  "-Dcom.sun.management.jmxremote.local.only=false",
  "-Dcom.sun.management.jmxremote.port=1099",
  "-Dcom.sun.management.jmxremote.rmi.port=1099",
  "-Djava.rmi.server.hostname=127.0.0.1"
)

javaOptions in reStart ++= (javaOptions in run).value
