with open("C:/Users/Administrator/Desktop/新建文件夹/jpetstore/src/main/java/com/jpetstore/controller/PageController.java", "r", encoding="utf-8") as f:
    content = f.read()

# Fix escaped quotes from PowerShell
content = content.replace('\\"', '"')

with open("C:/Users/Administrator/Desktop/新建文件夹/jpetstore/src/main/java/com/jpetstore/controller/PageController.java", "w", encoding="utf-8") as f:
    f.write(content)

print("Fixed PageController.java")
