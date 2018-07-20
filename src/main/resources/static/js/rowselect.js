onRowSelect = (function onselect(url, rowId, selId) {
    var rows = document.getElementsByClassName('selectable_row');
    for (var i = rows.length - 1; i >= 0; --i) {
        rows[i].className = 'selectable_row';

    }
    var selRow = document.getElementById(rowId);
    if (selRow != null) {
        selRow.className += ' bg-dark';
    }
    var context = '/';
    /*[+
            context = [[@{/}]];
        +]*/

    var edit = document.getElementById("edit_button");
    if (edit != null) {
        edit.href = context + url +"/edit?id=" + selId;
    }
    var view = document.getElementById("view_button");
    if (view != null) {
        view.href = context + url +"/view?id=" + selId;
    }
    var copy = document.getElementById("copy_button");
    if (copy != null) {
        copy.action = context + url +"/copy?id=" + selId;
    }
    var del = document.getElementById("delete_button");
    if (del != null) {
        del.action = context + url + "/delete?id=" + selId;
    }
});
