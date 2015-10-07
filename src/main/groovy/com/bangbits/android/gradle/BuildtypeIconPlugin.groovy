package com.bangbits.android.gradle

import java.awt.image.BufferedImage
import org.gradle.api.*
import groovy.io.FileType

class BuildtypeIconPlugin implements Plugin<Project> {
    
    @groovy.transform.Memoized
    static def capitalize(String string){
        return string.toLowerCase().tokenize().collect { it.capitalize() }.join(' ')
    }
    
    def void apply(Project project) {

        if(!project.plugins.hasPlugin('android')){
            throw new GradleException("Failed to locate android plugin. The buildtype-icon-overlay-plugin works in unison with Android, so please apply the 'com.android.application' plugin.")
        }
        
        installTasks(project)
    }

    private void installTasks(Project project) {

        def tpaInfoTaskAll = project.task('generateBuildtypeIcons',
                description: 'Generates overlayed icons for non-release builds') << { 

            // Possible bug lurking: We don't always have just one manifest and in main?!
            def manifestFile = new File("${project.projectDir}/src/main/AndroidManifest.xml")
            if(manifestFile.exists()){
                def manifest = new XmlSlurper().parse(manifestFile)
                def launcher = manifest.application.@'android:icon'
                def (folderName, fileName) = ((String)launcher).split('/')
                
                folderName = ((String)folderName).replace('@', '')
                
                generateImagesForVariant(project, folderName, fileName);
                
                project.android.productFlavors.all { productFlavor ->
                    generateImagesForVariant(project, folderName, fileName, productFlavor.name);
                }
            }else{
                println "Failed at locating file $manifestFile"
            }
        }
    }
    
    def void generateImagesForVariant(def project, String folderName, String fileName, String productFlavorName = ''){

        def resSourceFolder = constructResourceSourceFolder(project.projectDir, productFlavorName)

        //println "Looking for ${folderName}* directories in $resSourceFolder"
        resSourceFolder.eachFile(FileType.DIRECTORIES) { resDpiFolder ->
            if(resDpiFolder.name.startsWith(folderName)){
                def inputFile = new File("${resSourceFolder}/${resDpiFolder.name}/${fileName}.png")
                if(inputFile.exists()){

                    //println "Master icon for ${productFlavorName.isEmpty() ? "main":productFlavorName}: ${inputFile.getName()}"
                    BufferedImage inputImage = ImageStamper.readIntoBufferedImage(inputFile)
                    //println("+-Icon size: ${inputImage.getWidth()}x${inputImage.getHeight()}");
                    ImageColorStats imageStats = new ImageColorStats(inputImage)
                    //println imageStats.toString()
                    
                    project.android.buildTypes.all { buildType ->
                        if(!buildType.name.equalsIgnoreCase('release')){

                            // Create variant folder if it doesn't exist
                            def buildTypeName = (productFlavorName.isEmpty() ? buildType.name:capitalize(buildType.name))
                            def relativeDir = "src/${productFlavorName}${buildTypeName}/res/${resDpiFolder.name}"
                            def outputDir = new File("${project.projectDir}/${relativeDir}")
                            if(!outputDir.exists()){
                                outputDir.mkdirs()
                            }

                            // Generate overlayed variant icon
                            def outputFile = new File(outputDir, "${fileName}.png")
                            //println "+-Adding ${buildType.name} overlay"

                            ImageStamper.generateImage(inputImage, imageStats, buildType.name, outputFile)

                            println "+-Generated ${relativeDir}/${fileName}.png"
                        }
                    }
                }else{
                    println "Master icon for ${productFlavorName.isEmpty() ? "main":productFlavorName} not found, skipping!"
                }
            }
        }        
    }
    
    File constructResourceSourceFolder(def projectDir, def productFlavorName){
        if(productFlavorName.empty){
            return new File("${projectDir}/src/main/res")
        }else{
            return new File("${projectDir}/src/${productFlavorName}/res")
        }
    }    
}