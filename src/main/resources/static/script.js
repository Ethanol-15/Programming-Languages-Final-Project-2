const API_URL = "http://localhost:2424/api";

// Event listener for syntax checking form submission
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

// Redirect to payment page when the "Get 100 API calls for 1$!" button is clicked
document.getElementById("payButton").addEventListener("click", async () => {
    try {
        // Send a POST request to /paypal/makePayment to initiate the payment
        const response = await fetch("/paypal/makePayment?amount=1.00", {
            method: "POST",  // Ensure we're using POST here
            headers: {
                "Content-Type": "application/json",
            },
        });

        const approvalUrl = await response.text();  // The response should be the PayPal approval URL
        console.log("Response from payment API:", approvalUrl); // Log the response to inspect

        if (response.ok && approvalUrl.startsWith("http")) {
            // Redirect the user to PayPal for payment approval
            window.location.href = approvalUrl;  // This will take the user to the PayPal payment page
        } else {
            alert('Payment creation failed. Please try again.');
        }
    } catch (error) {
        console.error("Error:", error);
        alert('An error occurred while initiating the payment.');
    }
});
