#!/bin/bash
# railway-setup.sh

echo "=== Railway Setup Script ==="

# 1. Xóa các file config cũ
rm -f railway.json .railway.json

# 2. Tạo file cấu hình mới
cat > railway.toml << 'EOF'
[build]
builder = "NIXPACKS"
buildCommand = "./gradlew clean bootJar -x test"
startCommand = "java -jar build/libs/nekonihongo-backend.jar"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 30

[environments.production]
JAVA_OPTS = "-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"
EOF

# 3. Tạo file environment variables mẫu
cat > .env.railway << 'EOF'
# Railway Environment Variables
DATABASE_URL=jdbc:mysql://localhost:3306/nekonihongo_db
JWT_SECRET=your-secret-key-change-this
ALLOWED_ORIGINS=https://*.up.railway.app,http://localhost:5173
EOF

# 4. Cấp quyền cho gradlew
chmod +x gradlew

echo "✅ Setup completed!"
echo "📁 Files created:"
echo "   - railway.toml"
echo "   - .env.railway"
echo ""
echo "🚀 Next steps:"
echo "1. Push code to GitHub"
echo "2. Connect to Railway"
echo "3. Set environment variables in Railway dashboard"