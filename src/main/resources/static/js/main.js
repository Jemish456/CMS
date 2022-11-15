document.addEventListener("DOMContentLoaded", function (event) {
    document.getElementById("loader").remove();


    // fetch slots api
    // const doctorElement = document.getElementById('doctor');
    // const appointmentDateElement = document.getElementById('appointmentDate');
    // const startTimeGroup = document.getElementById('startTimeGroup');
    //
    // appointmentDateElement?.addEventListener('change', () => {
    //     let doctorId = doctorElement.value;
    //     let appointmentDate = appointmentDateElement.value;
    //     let data = {
    //         'doctorId':doctorId,
    //         'appointmentDate':appointmentDate
    //     };
    //     fetch('/api/doctor/slots', {
    //         method: 'POST', // or 'GET'
    //         body:JSON.stringify(data),
    //     })
    //         .then(response => response.json())
    //         .then(data => {
    //
    //             startTimeGroup.innerHTML = 'Please select appointment date.';
    //             if (data.length==0){
    //                 startTimeGroup.innerHTML = '<b>No slots available</b>'
    //             }else {
    //                 startTimeGroup.innerHTML = '';
    //                 data.forEach(function (currentValue, index) {
    //
    //                     const slotRadio = document.createElement("input");
    //                     slotRadio.setAttribute('type', 'radio');
    //                     slotRadio.setAttribute('class', 'btn-check');
    //                     slotRadio.setAttribute('name', 'startTime');
    //                     slotRadio.setAttribute('value', currentValue);
    //                     slotRadio.setAttribute('id', currentValue);
    //                     // slotRadio.setAttribute('required',true);
    //
    //                     const slotRadioLabel = document.createElement('label');
    //                     slotRadioLabel.setAttribute('class', 'btn btn-outline-success btn-rounded m-1');
    //                     slotRadioLabel.setAttribute('for', currentValue);
    //                     slotRadioLabel.innerHTML = currentValue;
    //
    //                     startTimeGroup.append(slotRadio);
    //                     startTimeGroup.append(slotRadioLabel);
    //                 });
    //             }
    //
    //         })
    //         .catch((error) => {
    //             console.error('Error:', error);
    //         });
    // });
    // end fetch slots api

    // fetch doctor list with search
    const doctorListTableBody = document.getElementById('doctorListTableBody');
    const searchDoctorElement = document.getElementById('searchDoctor');

    searchDoctorElement?.addEventListener('onkeyup', () => {

        let searchDoctor = searchDoctorElement.value;

        fetch('/api/doctor?search=' + searchDoctor, {
            method: 'GET', // or 'PUT'
            headers: {
                'Content-Type': 'application/json',
            },
            // body:{
            //     JSON.stringify(data)
            // },
        })
            .then(response => response.json())
            .then(data => {

                doctorListTableBody.innerHTML = '';
                data.forEach(function (currentValue, index) {
                    const tr = document.createElement("tr");

                    const tdFirstName = document.createElement("td");
                    tdFirstName.innerHTML = currentValue.firstName;
                    tr.append(tdFirstName);

                    const tdLastName = document.createElement("td");
                    tdLastName.innerHTML = currentValue.lastName;
                    tr.append(tdLastName);

                    const tdEmail = document.createElement("td");
                    tdEmail.innerHTML = currentValue.email;
                    tr.append(tdEmail);

                    const tdMobileNumber = document.createElement("td");
                    tdMobileNumber.innerHTML = currentValue.mobileNumber;
                    tr.append(tdMobileNumber);

                    const tdPhoto = document.createElement("td");
                    const imgPhoto = document.createElement('img');
                    imgPhoto.setAttribute('src', '/display/image/id=' + currentValue.id);
                    imgPhoto.setAttribute('class', 'rounded-circle');
                    imgPhoto.setAttribute('height', '100px');
                    imgPhoto.setAttribute('width', '100px');
                    tdPhoto.append(imgPhoto);
                    tr.append(tdPhoto);

                    const tdActions = document.createElement("td");
                    const hrefView = document.createElement("a");
                    hrefView.setAttribute("class", "btn btn-info");
                    hrefView.setAttribute("href", "/patient/doctor/view?id=" + currentValue.id);
                    hrefView.innerHTML = "<i class=\"fa fa-eye\"></i>";
                    tdActions.append(hrefView);
                    const hrefAppointment = document.createElement("a");
                    hrefAppointment.setAttribute('class', 'btn btn-success');
                    hrefAppointment.setAttribute('href', '/patient/appointment/book?doctorId=' + currentValue.id)
                    hrefAppointment.innerHTML = "<i class=\"fas fa-calendar-check\"></i>";
                    tdActions.append(hrefAppointment);
                    tr.append(tdActions);

                    doctorListTableBody.append(tr);
                });

            })
            .catch((error) => {
                console.error('Error:', error);
            });
    });

    // end fetch doctor list with search


});

$(document).ready(function () {
    $('#button-addon3').click('change', function (e) {
        let searchParams = new URLSearchParams(window.location.search)
        let patientId = searchParams.get('patientId')
        // let appointmentDate = $('#appointmentDate').val();
        $.ajax({
            url: '/chat/chatting',
            type: 'GET',
            data: {
                'patientId': patientId
            },
            success: function (data) {
                $('#chat-history').html("");
                data.forEach(function (currentValue) {
                    $('#chat-history').append(
                        "<input type=\"radio\" class=\"btn-check\" name='startTime' value='" + currentValue
                        + "' id='" + currentValue + "' autocomplete=\"off\">\n" +
                        "<label class=\"btn btn-outline-success btn-rounded m-1\" for='" + currentValue + "'>" + currentValue + "</label>"
                    );
                });
            }
        });
    });
});

function fetchAppointmentSlots(doctorId,appointmentDate){
    const startTimeGroup = $('#startTimeGroup');
    let data = {
        'doctorId':doctorId,
        'appointmentDate':appointmentDate
    };
    console.log(data);
    $.ajax({
        url: '/api/doctor/slots',
        type: 'POST',
        data: data,
        success: function (data) {
            startTimeGroup.innerHTML = 'Please select appointment date.';
            if (data.length==0){
                startTimeGroup.html('<b>No slots available</b>');
            }else {
                startTimeGroup.html('');
                data.forEach(function(currentValue){

                    const slotRadio = document.createElement("input");
                    slotRadio.setAttribute('type', 'radio');
                    slotRadio.setAttribute('class', 'btn-check');
                    slotRadio.setAttribute('name', 'startTime');
                    slotRadio.setAttribute('value', currentValue);
                    slotRadio.setAttribute('id', currentValue);

                    const slotRadioLabel = document.createElement('label');
                    slotRadioLabel.setAttribute('class', 'btn btn-outline-success btn-rounded m-1');
                    slotRadioLabel.setAttribute('for', currentValue);
                    slotRadioLabel.innerHTML = currentValue;

                    startTimeGroup.append(slotRadio);
                    startTimeGroup.append(slotRadioLabel);
                });
            }
        }
    });
}

$(document).ready(function (e) {
    $('#appointmentDate').on('change',function (e) {
        let doctorId = $('#doctor').val();
        let appointmentDate = $('#appointmentDate').val();
        fetchAppointmentSlots(doctorId,appointmentDate);
    });


    $('#toggle-password').on('click',function (e) {
       alert("jdsf");
    });

});

