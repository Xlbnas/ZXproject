#!/bin/sh
# 首次启动时把内置的种子图片复制到 uploads 卷
SEED_DIR="/app/seed-images"
UPLOAD_DIR="/app/uploads"

if [ -d "$SEED_DIR" ]; then
    # 遍历种子图片，只复制 uploads 里不存在的文件（不覆盖用户上传的）
    find "$SEED_DIR" -type f | while read src; do
        # 保留相对路径结构：seed-images/books/book1.jpg → uploads/books/book1.jpg
        rel="${src#$SEED_DIR/}"
        dst="$UPLOAD_DIR/$rel"
        dst_dir="$(dirname "$dst")"
        if [ ! -f "$dst" ]; then
            mkdir -p "$dst_dir"
            cp "$src" "$dst"
        fi
    done
    echo "图片初始化完成"
fi

exec java $JAVA_OPTS -jar app.jar
