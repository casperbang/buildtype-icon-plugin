package com.bangbits.android.gradle

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.awt.image.BufferedImage
import groovy.io.FileType

class BatchProcessingTest {

    public BatchProcessingTest() {
    }
    
    @Test
    public void runBatchJob() {
        
        def random = new Random()
        def labels = ['Beta', 'Develop', 'Debug', 'Staging', 'Test', 'Preview']

        File sourceFolder = new File(new File(".").getAbsoluteFile().getParent(), "src/test/resources")
        File destinationFolder = new File(new File(".").getAbsoluteFile().getParent(), "build/icons")
        destinationFolder.mkdir()
        
        sourceFolder.eachFile(FileType.FILES) { file ->
            if(file.name.endsWith('png')){
                def inputFile = new File("${sourceFolder}/${file.name}")

                if(inputFile.exists()){
                    BufferedImage inputImage = ImageStamper.readIntoBufferedImage(inputFile)
                    ImageColorStats imageStats = new ImageColorStats(inputImage)
                    def outputFile = new File("${destinationFolder}/${file.name}")
                    ImageStamper.generateImage(inputImage, imageStats, labels[random.nextInt(labels.size())], outputFile)
                    println "+-Generated $outputFile"
                }                
            }
        }
    }
}
