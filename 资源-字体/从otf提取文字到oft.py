from fontTools import subset

# 输入文件
font_path = "PingFangSC-Regular.otf"
#font_path = "PingFangSC-Semibold.otf"

# 输出文件
output_path = "PingFangSC-Regular-subset.otf"
#output_path = "PingFangSC-Semibold-subset.otf"
# 要保留的字符文件
text_file = "3500+symbols.txt"

# 读取字符集
with open(text_file, "r", encoding="utf-8") as f:
    text = f.read()

# === 手动补充保留字符 ===
extra_chars = [
    " ",        # 空格
    "\u3000",   # 全角空格
    "\n",       # 换行符
    "\t",       # 制表符
    ".,!?;:()[]{}<>“”‘’\"'`~@#$%^&*-_=+|\\/→←↑↓",
]
# 拼接进去
text += "".join(extra_chars)

# 配置子集化参数
options = subset.Options()
options.layout_features = ["*"]   # 保留所有 OpenType 功能
options.drop_tables = []          # 等于 --drop-tables=
options.notdef_glyph = True       # 保留 .notdef 字形
options.notdef_outline = True     # 保留 .notdef 的轮廓
options.glyph_names = True        # 保留字形名字

# 创建子集器
subsetter = subset.Subsetter(options=options)
# 填充字符集
subsetter.populate(text=text)

# 载入字体
font = subset.load_font(font_path, options)

# 执行子集化
subsetter.subset(font)

# 保存结果
subset.save_font(font, output_path, options)

print(f"✅ 子集化完成：{output_path}")
