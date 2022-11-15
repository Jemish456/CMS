var currentPageNumber,rowPerPage;
var totalPage;
function printingTable(appointments,tbody){

    const tr = document.createElement("tr");
    const patientName = document.createElement("td");
    const description = document.createElement("td");
    const startDateTime = document.createElement("td");
    const endDateTime = document.createElement("td");
    const action = document.createElement("td");
    const appointmentLink = document.createElement("a");
    const appointmentIcon = document.createElement("i");
    const appointmentApprovedLink = document.createElement("a");
    const appointmentApprovedIcon = document.createElement("i");
    const appointmentDeleteLink = document.createElement("a");
    const appointmentDeleteIcon = document.createElement("i");

    patientName.innerHTML = appointments.patient.firstName + ' ' + appointments.patient.lastName;
    tr.appendChild(patientName);

    description.innerHTML = appointments.description;
    tr.appendChild(description);

    startDateTime.innerHTML = appointments.startDateTime;
    tr.appendChild(startDateTime);

    endDateTime.innerHTML = appointments.endDateTime;
    tr.appendChild(endDateTime);

    appointmentLink.setAttribute("title","View Appointment");
    appointmentLink.setAttribute("class","btn btn-info mb-1");
    appointmentLink.setAttribute("style","margin:0px 2.5px");
    appointmentLink.setAttribute("href","/appointment/view/?appointmentId="+ appointments.id);
    appointmentIcon.setAttribute("class","fa fa-eye");
    appointmentLink.appendChild(appointmentIcon);
    action.appendChild(appointmentLink);

    appointmentApprovedLink.setAttribute("title","Approved");
    appointmentApprovedLink.setAttribute("class","btn btn-success mb-1");
    appointmentApprovedLink.setAttribute("style","margin:0px 2.5px");
    appointmentApprovedLink.setAttribute("href","/appointment/approve/?id="+ appointments.id);
    appointmentApprovedIcon.setAttribute("class","fas fa-check");
    appointmentApprovedLink.appendChild(appointmentApprovedIcon);
    action.appendChild(appointmentApprovedLink);

    appointmentDeleteLink.setAttribute("title","Delete");
    appointmentDeleteLink.setAttribute("class","btn btn-danger mb-1");
    appointmentDeleteLink.setAttribute("style","margin:0px 2.5px");
    appointmentDeleteLink.setAttribute("href","/appointment/delete/?id="+ appointments.id);
    appointmentDeleteIcon.setAttribute("class","fas fa-times");
    appointmentDeleteLink.appendChild(appointmentDeleteIcon);
    action.appendChild(appointmentDeleteLink);



    tr.appendChild(action);


    tbody.appendChild(tr);

}

const search=(page)=>{

    currentPageNumber = page;
    rowPerPage = document.getElementById("row_per_page").value;

    const paginationDiv = document.getElementById("pagination-div");

    let keywords = $("#search-input").val();

    const tbody  = document.getElementById("dataTable");
    tbody.innerHTML = '';

    // let url = '/doctor/search/?keywords='+keywords+'&page='+page+'&size='+rowPerPage;
    let url = '/doctor/search/';
    const formData = new FormData();
    formData.append('keywords',keywords);
    formData.append('page',page);
    formData.append('size',rowPerPage);
    fetch(url, {
        method: 'POST',
        body: formData
    })
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            if(data.content.length <= 0){
                paginationDiv.style.display = "none";
                tbody.innerHTML = '';
                const msgTR = document.createElement("tr");
                const msgTD = document.createElement("td")

                msgTD.setAttribute("colspan","5")
                msgTD.innerHTML = "No Patient found"
                msgTD.style = "text-align: center; font-size:40px";
                msgTR.appendChild(msgTD)
                tbody.appendChild(msgTR);
            } else {

                paginationDiv.style.display = "block";

                for (let i=0;i<data.content.length;i++){
                    printingTable(data.content[i],tbody)
                }
                const div = document.getElementById("number-of-pages");
                div.innerHTML = '';
                totalPage = data.totalPages;

                for (let i=1;i<=data.totalPages;i++){

                    const li = document.createElement("li");
                    const a = document.createElement("a");

                    li.setAttribute("class","page-item");
                    a.setAttribute("class","page-link");
                    a.innerHTML = i;
                    a.onclick = ()=>{changePageValue(i)}
                    li.appendChild(a);
                    div.appendChild(li);
                }
            }
        });
}

const changeRowValue=()=>{

    search(0);
}

const getPreviousPage=()=>{


    var previousPage = currentPageNumber - 1;
    if(previousPage < 0 ) {
        previousPage = 0;
    }

    search(previousPage);

}

const getNextPage=()=>{



    var nextPage = currentPageNumber + 1 ;

    if(nextPage >= totalPage) {
        nextPage = totalPage-1;
    }

    search(nextPage);

}

function changePageValue(page){

    currentPageNumber = page - 1;
    search(currentPageNumber);
}
