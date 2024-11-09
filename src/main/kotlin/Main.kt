package com.woznes.fakeSSH
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.sshd.common.keyprovider.KeyPairProvider
import org.apache.sshd.common.session.SessionContext
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyPairGenerator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val csvFilePath = "fakeSSH.csv"

fun setupCsvFile() {
    if (!Files.exists(Paths.get(csvFilePath))) {
        FileWriter(csvFilePath, true).use { writer ->
            CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Username", "Password")).use { printer ->
                printer.flush()
            }
        }
    }
}

fun logAttemptToCsv(username: String, password: String) {
    FileWriter(csvFilePath, true).use { writer ->
        CSVPrinter(writer, CSVFormat.DEFAULT).use { printer ->
            printer.printRecord(username, password)
            printer.flush()
        }
    }
}
fun setupLogger(logPath: String) {
    val fileHandler = FileHandler(logPath, true)
    fileHandler.formatter = SimpleFormatter()
    logger.addHandler(fileHandler)
}

fun getCurrentTime(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
    return LocalDateTime.now().format(formatter)
}

fun logAttempt(address: String, username: String, password: String) {
    println("${getCurrentTime()} Attempt from: ${address}, Username: $username, Password: $password")
    logAttemptToCsv(username, password)
}
val generateRandomKeyPair= {s: SessionContext -> 
    val keyGen = KeyPairGenerator.getInstance("RSA")
    keyGen.initialize(2048) // 设置密钥大小为2048位
    mutableListOf(keyGen.generateKeyPair())
}

fun startSshServerWithAuth() {
    val sshd = SshServer.setUpDefaultServer()
    sshd.port = 2222
    sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider())
    
    sshd.passwordAuthenticator = PasswordAuthenticator { username, password, session ->
        logAttempt(session.clientAddress.toString(), username, password)
        false
    }


    try {
        sshd.start()
        println("Fake SSH Server started on port ${sshd.port} with a random fingerprint")

        // 无限循环保持服务器运行
        while (true) {
            Thread.sleep(1000) // 每秒休眠一次，保持主线程不退出
        }

    } catch (e: Exception) {
        println("Failed to start SSH server: ${e.message}")
        e.printStackTrace()
    } finally {
        sshd.stop() // 在退出之前停止SSH服务器
    }
}

fun main() {
    setupLogger("fakeSSH.log")
    setupCsvFile()
    startSshServerWithAuth()
}
