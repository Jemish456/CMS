var totalDays = document.getElementById('total-days');
var totalWorkingDay = document.getElementById('total-working-day');
var salaryPerDays = document.getElementById('salary-per-days');
var totalLeave = document.getElementById('total-leave');


//Showing Detail Of Salary Of that month by month and staffMember Ajax
function showSalary( monthId,memberId) {


    var formData = new FormData();
    formData.set("id",memberId);
    formData.set("month",monthId);
    $.ajax({
        url:'http://localhost:8080/api/salaryDetails',
        type:'POST',
        data:formData,
        processData: false,
        contentType:false,
        error: function(jqXHR,textStatus,errorMessage){
            console.log(errorMessage);
        },
        success:function (response) {
            // var textNode = document.createTextNode(response[0]);
            // totalDays.appendChild(textNode);
            totalDays.innerHTML=response[0];
            totalWorkingDay.innerHTML=response[1];
            salaryPerDays.innerHTML=response[2];
            totalLeave.innerHTML=response[3];

        }
    })
}