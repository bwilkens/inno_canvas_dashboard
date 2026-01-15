#!/bin/sh
set -e  # Stop bij fouten

# Check of script als root draait
if [ "$(id -u)" -ne 0 ]; then
    echo "[!] Dit script moet met sudo of als root worden uitgevoerd."
    exit 1
fi

# Update package lists
apt-get update

# Maak directory aan
mkdir -p /opt/actions-runner && cd /opt/actions-runner

# Vraag om user voor ownership
read -p "Als welke user wil je de directory ownen: " user

# Download de laatste runner package
curl -O -L https://github.com/actions/runner/releases/download/v2.329.0/actions-runner-linux-x64-2.329.0.tar.gz

# Extract de installer
tar xzf actions-runner-linux-x64-2.329.0.tar.gz

# Ownership aanpassen
chown -R "$user:$user" /opt/actions-runner

# Vraag voor Repo URL
read -p "Wat is de URL van je Repository: " repo_url

# Vraag om Repo Token
read -p "Wat is de token van je Repository: " repo_token

# Configuratie uitvoeren als gekozen user
sudo -u "$user" ./config.sh --url "$repo_url" --token "$repo_token"

# Systemd file aanmaken
cat <<EOF > /etc/systemd/system/runner.service
[Unit]
Description=GitHub Actions Runner
After=network-online.target

[Service]
Type=simple
ExecStart=/opt/actions-runner/run.sh
Restart=always
RestartSec=5
User=$user

[Install]
WantedBy=multi-user.target
EOF

# Service activeren
systemctl daemon-reload
systemctl enable runner.service
systemctl start runner.service

echo "✅ Runner geïnstalleerd en systemd service actief!"
