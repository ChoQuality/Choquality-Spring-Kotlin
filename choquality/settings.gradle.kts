rootProject.name = "choquality"

include("ProxyMain", "common", "todo")

project(":common").projectDir = file("./BuildComponent/common")
project(":todo").projectDir = file("./BuildComponent/todo")
