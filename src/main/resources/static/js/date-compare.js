console.log("hello");

$(document).ready(function(){
    $("#txtFromDate").datepicker({
        numberOfMonths: 2,
        onSelect: function(selected) {
            $("#txtToDate").datepicker("option","minDate", selected)
        }
    });
    $("#txtToDate").datepicker({
        numberOfMonths: 2,
        onSelect: function(selected) {
            $("#txtFromDate").datepicker("option","maxDate", selected)
        }
    });
});z