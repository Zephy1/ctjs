plugins {
    kotlin("jvm") version "2.0.21" apply false
    id("gg.essential.loom") version "1.9.+" apply false
    id("gg.essential.multi-version.root")
}

preprocess {
    val fabric12104 = createNode("1.21.4-fabric", 12104, "yarn")
    val fabric12105 = createNode("1.21.5-fabric", 12105, "yarn")
    val fabric12106 = createNode("1.21.6-fabric", 12106, "yarn")
    val fabric12107 = createNode("1.21.7-fabric", 12107, "yarn")
    val fabric12108 = createNode("1.21.8-fabric", 12108, "yarn")
    val fabric12109 = createNode("1.21.9-fabric", 12109, "yarn")
    val fabric12110 = createNode("1.21.10-fabric", 12110, "yarn")
    val fabric12111 = createNode("1.21.11-fabric", 12111, "yarn")

    fabric12105.link(fabric12104)
    fabric12105.link(fabric12106)
    fabric12106.link(fabric12107)
    fabric12107.link(fabric12108)
    fabric12108.link(fabric12109)
    fabric12109.link(fabric12110)
    fabric12110.link(fabric12111)
}