const API_URL = "http://localhost:2424/api";

document.getElementById("syntaxForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const codeInput = document.getElementById("codeInput").value.trim();
    const resultDiv = document.getElementById("result");
    const statsDiv = document.getElementById("stats");

    resultDiv.textContent = "";
    statsDiv.textContent = "";

    if (!codeInput) {
        resultDiv.textContent = "Please enter some code.";
        return;
    }

    try {
        // POST request to check syntax
        const response = await fetch(`${API_URL}/check`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ code: codeInput }),
        });

        const data = await response.json();

        if (response.ok) {
            resultDiv.textContent = `Syntax Check Result: ${data.result}`;
        } else {
            resultDiv.textContent = data.message;
        }

        // GET request to fetch stats
        const statsResponse = await fetch(`${API_URL}/stats`);
        const statsData = await statsResponse.json();

        if (statsResponse.ok) {
            statsDiv.innerHTML = `Total API Calls: ${statsData.totalCalls} <br> Remaining Calls: ${statsData.remainingCalls}`;
        } else {
            statsDiv.textContent = "Could not fetch stats.";
        }
    } catch (error) {
        console.error("Error:", error);
        resultDiv.textContent = "An error occurred while connecting to the server.";
        statsDiv.textContent = "Could not fetch stats.";
    }
});
