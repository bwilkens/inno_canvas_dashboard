package nl.hu.inno.dashboard.fileparser.application

import nl.hu.inno.dashboard.exception.exceptions.FileTypeNotSupportedException
import nl.hu.inno.dashboard.filefetcher.application.FileFetcherServiceImpl
import nl.hu.inno.dashboard.fileparser.domain.FileParser
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class FileParserServiceImpl(
    private val fileParsers: List<FileParser>,
) : FileParserService {

    companion object {
        private val log = LoggerFactory.getLogger(FileFetcherServiceImpl::class.java)
    }

    override fun parseFile(resource: Resource): List<List<String>> {
        log.debug("parseFile requested: filename={}", resource.filename)

        val parser = selectParser(resource)
        log.debug(
            "Parser selected: parserClass={}, filename={}",
            parser::class.simpleName,
            resource.filename
        )
        return parser.parse(resource)
    }

    private fun selectParser(resource: Resource): FileParser {
        for (parser in fileParsers) {
            if (parser.supports(resource)) {
                log.debug(
                    "Parser supports resource: parserClass={}, filename={}",
                    parser::class.simpleName,
                    resource.filename
                )
                return parser
            }
        }
        log.warn(
            "No parser found for resource: filename={}, availableParsers={}",
            resource.filename,
            fileParsers.map { it::class.simpleName }
        )

        throw FileTypeNotSupportedException("No available parser found for file: ${resource.filename}")
    }
}