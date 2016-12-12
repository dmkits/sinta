/**
 * Created by ianagez on 20.10.16.
 */


//function loadDataFromServer (url, error_callback) {
//    var data;
//    var xhr = new XMLHttpRequest();
//    xhr.open('GET', url, false);
//    xhr.send();
//    if (xhr.status != 200) {
//        //alert(xhr.status + ': ' + xhr.statusText); // пример вывода: 404: Not Found
//        if (error_callback) error_callback();
//        return null;
//    } else {
//        // вывести результат
//        //            alert( xhr.responseText ); // responseText -- текст ответа.
//        var dataText = xhr.responseText;
//        data = JSON.parse(dataText);
//        return data;
//    }
//}

function loadDataFromServer (url, success, error) {
    var data;
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, false);
    xhr.send();
    if (xhr.status != 200) {
        //alert(xhr.status + ': ' + xhr.statusText); // пример вывода: 404: Not Found
        if (error) error(xhr.responseText);
       // if (error) error();
        return;
    } else {
        // вывести результат
        //            alert( xhr.responseText ); // responseText -- текст ответа.
        var dataText = xhr.responseText;
        data = JSON.parse(dataText);
        if (success) success(data);
    }
}
