function loadMessages() {

    this.source = null;

    this.start = function () {

        var userId = location.pathname.split('/')[2]

        var messagesTable = document.getElementById("messages");

        this.source = new EventSource("/user/" + userId + "/messages");

        this.source.addEventListener("message", function (event) {
            // These events are JSON, so parsing and DOM fiddling are needed
            var message = JSON.parse(event.data);

            var row = messagesTable.getElementsByTagName("tbody")[0].insertRow(0);
            var cell0 = row.insertCell(0);
            var cell1 = row.insertCell(1);

            cell0.className = "author-style";
            cell0.innerHTML = message.author;

            cell1.className = "text";
            cell1.innerHTML = message.text;
        });

        this.source.onerror = function () {
            this.close();
        };

    };

    this.stop = function () {
        this.source.close();
    }

}

var message = new loadMessages();

/*
 * Register callbacks for starting and stopping the SSE controller.
 */
window.onload = function () {
    message.start();
};
window.onbeforeunload = function () {
    message.stop();
}
