package nl.hu.inno.dashboard.fileparser.domain

import org.springframework.web.multipart.MultipartFile

interface FileParser {
    fun supports(file : MultipartFile) : Boolean
    fun parse(file : MultipartFile) : List<List<String>>
}