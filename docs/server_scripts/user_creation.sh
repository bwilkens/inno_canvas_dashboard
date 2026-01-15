#!/bin/bash

# Gebruik: sudo ./create_user.sh gebruikersnaam

USERNAME="$1"

if [ -z "$USERNAME" ]; then
  echo "Gebruik: $0 <gebruikersnaam>"
  exit 1
fi

# User aanmaken met home directory
if id "$USERNAME" &>/dev/null; then
  echo "User $USERNAME bestaat al."
else
  useradd -m -s /bin/bash "$USERNAME"
  echo "User $USERNAME aangemaakt."
fi

HOME_DIR="/home/$USERNAME"
API_FILE="$HOME_DIR/api.key"

# Zorg dat home directory bestaat
mkdir -p "$HOME_DIR"
chown "$USERNAME:$USERNAME" "$HOME_DIR"
chmod 700 "$HOME_DIR"

# API file aanmaken
if [ ! -f "$API_FILE" ]; then
  touch "$API_FILE"
  chown "$USERNAME:$USERNAME" "$API_FILE"
  chmod 600 "$API_FILE"
  echo "API file aangemaakt op $API_FILE"
else
  echo "API file bestaat al."
fi
