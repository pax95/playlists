$(document).ready(function () {

    var socket, socket2;

    $('#connect_form2').submit(function () {

        var host2 = $("#connect_url2").val();
        socket2 = new WebSocket(host2);

        // Add a connect listener
        socket2.onopen = function () {
            $('#msg').append('<p class="event">Socket2 Status: ' + socket2.readyState + ' (open)</p>');
        }

        socket2.onmessage = function (msg) {
            $('#msg').append('<p class="message">Received2: ' + msg.data + "</p>");
        }

        socket2.onclose = function () {
            $('#msg').append('<p class="event">Socket2 Status: ' + socket2.readyState + ' (Closed)</p>');
        }

        return false;
    });


    $('#connect_form').submit(function () {

        var playTable = document.getElementById("playTable");
        var playRowIndexes = {};
        var host = $("#connect_url").val();
        socket = new WebSocket(host);

        $('#connect').fadeOut({ duration:'fast' });
        $('#disconnect').fadeIn();
        $('#send_form_input').removeAttr('disabled');

        // Add a connect listener
        socket.onopen = function () {
            $('#msg').append('<p class="event">Socket Status: ' + socket.readyState + ' (open)</p>');
        }

        socket.onmessage = function (msg) {
           // $('#msg').append('<p class="message">Received: ' + msg.data + "</p>");

            var current = JSON.parse(msg.data);

            // extract the stock data fields
            var channel = current.channel;
            var start = current.time;
            var title = current.title;
            var artist = current.artist;
            var album = current.album;
            var albumImage = current.albumPicture;
            if (albumImage != null) {
            	album = "<img src='"+albumImage+"' title='"+ album + "'/>";
            }
            // lookup the table row
            var playRowIndex = playRowIndexes[channel];
            var playRow = playTable.rows[playRowIndex];

            // lazily populate the table row, with 6 cells
            if (playRow === undefined) {
                var playRowIndex = playTable.rows.length;
                playRow = playTable.insertRow(playRowIndex);
                for (var cell = 0; cell < 5; cell++) {
                    playRow.insertCell(cell);
                }
                playRow.cells[0].className = 'symbol';
                playRow.cells[1].className = 'open';
                playRow.cells[2].className = 'last';
                playRow.cells[3].className = 'change';
                playRow.cells[4].className = 'change';
                playRowIndexes[channel] = playRowIndex;
            }


            // detect price change

            // update the table row cell data
            playRow.cells[0].innerHTML = channel;
            playRow.cells[1].innerHTML = start;
            playRow.cells[2].innerHTML = title;
            playRow.cells[3].innerHTML = artist;
            playRow.cells[4].innerHTML = album;

        }

        socket.onclose = function () {
            $('#msg').append('<p class="event">Socket Status: ' + socket.readyState + ' (Closed)</p>');
        }

        return false;
    });

    $('#disconnect_form').submit(function () {

        socket.close();
        socket2.close();

        $('#msg').append('<p class="event">Socket Status: ' + socket.readyState + ' (Closed)</p>');
        $('#disconnect').fadeOut({ duration:'fast' });
        $('#connect').fadeIn();
        $('#send_form_input').addAttr('disabled');

        return false;
    });

});
