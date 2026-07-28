import re

with open("C:/Users/Administrator/Desktop/新建文件夹/sql/schema.sql", "r", encoding="utf-8") as f:
    content = f.read()

content = re.sub(r"\),, (\d+\.\d+), 'ON_SALE'\)", r", \1, 'ON_SALE')", content)
content = re.sub(r"\),, (\d+\.\d+), 'ON_SALE'\);", r", \1, 'ON_SALE');", content)
import re

with open("C:/Users/Administrator/Desktop/新建文件夹/sql/schema.sql", "r", encoding="utf-8") as f:
    content = f.read()

content = re.sub(r"\),, (\d+\.\d+), 'ON_SALE'\)", r", \1, 'ON_SALE')", content)
content = re.sub(r"\),, (\d+\.\d+), 'ON_SALE'\);", r", \1, 'ON_SALE');", content)

import re

with open("C:/Users/Administrator/Desktop/新建文件夹/sql/schema.sql", "r", encoding="utf-8") as f:
    lines = f.readlines()

result = []
for line in lines:
    stripped = line.strip()
    # Fix product rows that are missing price and status
    if "(" in stripped and stripped.count(",") >= 5 and "VALUES" not in stripped:
        parts = stripped.split(",")
        # Product rows have: ('ID', 'CAT', 'NAME', 'DESC', 'IMAGE'),
        # Should be: ('ID', 'CAT', 'NAME', 'DESC', 'IMAGE', PRICE, 'STATUS'),
        # Count the number of comma-separated values
        vals = [p.strip() for p in stripped.strip("(),;").split(",")]
        if len(vals) == 5 and all("'" in v for v in vals[:4]):
            # This is a product row without price and status
            last_char = stripped.rstrip()[-1]
            if last_char == ",":
                line = line.rstrip() + " 18.50, 'ON_SALE'),\n"
            elif last_char == ";":
                line = line.rstrip().rstrip(")") + ", 18.50, 'ON_SALE');\n"
    result.append(line)

with open("C:/Users/Administrator/Desktop/新建文件夹/sql/schema.sql", "w", encoding="utf-8") as f:
    f.writelines(result)

print("Schema fixed successfully")
