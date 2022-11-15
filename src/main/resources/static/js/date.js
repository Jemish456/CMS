console.log("Hello")
console.log(new Date())

$(function () {
    $('#appointmentDate').datepicker({
        minDate:new Date()
    });
});

