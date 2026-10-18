
const log = document.getElementById("battleLog");
const statusText = document.getElementById("status");

let gameStarted = false;
let busy = false;

function addLog(message) {
    const line = document.createElement("div");
    line.textContent = message;
    log.appendChild(line);
    log.scrollTop = log.scrollHeight;
}

async function callGameAPI(endpoint, method = "POST") {
    if (busy) return;

    busy = true;

    try {
        const response = await fetch(endpoint, { method });

        if (!response.ok) {
            throw new Error("Server returned HTTP " + response.status);
        }

        const data = await response.json();
        updateScreen(data);

    } catch (error) {
        addLog("Error: " + error.message);
        addLog("Make sure Spring Boot is running.");
        console.error(error);
    } finally {
        busy = false;
    }
}

function updateScreen(data) {
    if (data.message) addLog(data.message);

    if (data.playerHealth !== undefined) {
        const hp = Math.max(0, Math.min(100, data.playerHealth));
        document.getElementById("playerHealth").style.width = hp + "%";
        document.getElementById("playerHealth").textContent = hp + "%";

        document.getElementById("playerStats").textContent =
            `HP: ${hp} | Level: ${data.playerLevel} | XP: ${data.playerXp} | Coins: ${data.playerCoins}`;
    }


    if (data.enemyHealth !== undefined) {
        const hp = Math.max(0, Math.min(100, data.enemyHealth));
        const enemyBar = document.getElementById("enemyHealth");

        enemyBar.style.width = hp + "%";
        enemyBar.textContent = hp > 0 ? hp + "%" : "";

        document.getElementById("enemyStats").textContent =
            `${data.enemyName} | HP: ${hp}`;
    }

    if (data.stage !== undefined) {
        statusText.textContent = `Stage ${data.stage}`;
    }


    if (data.gameOver) {
        if (data.message && data.message.includes("Victory!")) {
            statusText.textContent = "🏆 Victory!" +
                                    " You conquered the arena!";
        } else if (data.message && data.message.includes("defeated")) {
            statusText.textContent = "💀 Game Over — You were defeated!";
        } else if (data.message && data.message.includes("left the arena")) {
            statusText.textContent = "You left the arena.";
        } else {
            statusText.textContent = "Game Over";
        }

        gameStarted = false;


        document.getElementById("attackBtn").disabled = true;
        document.getElementById("defendBtn").disabled = true;
        document.getElementById("healBtn").disabled = true;
        document.getElementById("runBtn").disabled = true;



    }

}


document.getElementById("newGameBtn").addEventListener("click", async () => {
    const nameInput = document.getElementById("playerNameInput");
    const playerName = nameInput.value.trim();

    if (!playerName) {
        addLog("Please enter your player name first.");
        nameInput.focus();
        return;
    }

    log.textContent = "";
    gameStarted = false;

    document.getElementById("attackBtn").disabled = false;
    document.getElementById("defendBtn").disabled = false;
    document.getElementById("healBtn").disabled = false;
    document.getElementById("runBtn").disabled = false;

    await callGameAPI(
        "/api/game/new?name=" + encodeURIComponent(playerName)
    );

    gameStarted = true;
});


document.getElementById("attackBtn").addEventListener("click", () => {
    if (!gameStarted) return addLog("Click New Game first.");
    callGameAPI("/api/game/action/attack");
});

document.getElementById("defendBtn").addEventListener("click", () => {
    if (!gameStarted) return addLog("Click New Game first.");
    callGameAPI("/api/game/action/defend");
});

document.getElementById("healBtn").addEventListener("click", () => {
    if (!gameStarted) return addLog("Click New Game first.");
    callGameAPI("/api/game/action/heal");
});

document.getElementById("runBtn").addEventListener("click", () => {
    if (!gameStarted) return addLog("Click New Game first.");
    callGameAPI("/api/game/action/run");
});



document.getElementById("profileBtn").addEventListener("click", async () => {
    try {
        const response = await fetch("/api/game/profile");
        const data = await response.json();

        if (!response.ok) {
            throw new Error("HTTP " + response.status);
        }

        if (data.success) {
            addLog("Saved Player Profile: " + data.profile);
            alert(data.profile);
        } else {
            addLog(data.message || "Could not load player profile.");
        }
    } catch (error) {
        addLog("Could not load player profile. Check the server.");
        console.error(error);
    }
});


document.getElementById("leaderboardBtn").addEventListener("click", async () => {
    try {
        const response = await fetch("/api/game/leaderboard");

        if (!response.ok) {
            throw new Error("HTTP " + response.status);
        }

        const data = await response.json();

        if (data.players && data.players.length > 0) {
            addLog("🏆 LEADERBOARD");
            data.players.forEach((player, index) => {
                addLog((index + 1) + ". " + player);
            });
        } else {
            addLog("No saved players found yet.");
        }
    } catch (error) {
        addLog("Could not load leaderboard. Check the server.");
        console.error(error);
    }
});
