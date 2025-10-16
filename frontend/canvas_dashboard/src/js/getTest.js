async function callBackend() {
    try {
        let getCourse = await fetch("http://localhost:8080/dashboard/1");
        let data = await getCourse.json();
        document.getElementById("getCanvasText").innerHTML = JSON.stringify(data);
    } catch (error) {
        document.getElementById("getCanvasText").innerHTML = "Error fetching data";
    }
}