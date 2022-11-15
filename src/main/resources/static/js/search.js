var currentPage;
var totalPages;


function getPageNumbers(totalPages,pagespan,flag){

    const pages = []

    pages.push("0")

    if(!flag) {
        if (currentPage > 1) {
            pages.push("....")
        }
        if (!pages.includes(currentPage.toString())) {
            pages.push(currentPage.toString())
        }
        if (currentPage < totalPages - 1) {
            pages.push("....")
        }
        if (!pages.includes(totalPages.toString())) {
            pages.push(totalPages.toString())
        }
        console.log(pages)

    }

    pages.forEach(page => {
        let l = document.createElement("li");

        if(+page === currentPage)
            l.setAttribute("class","page-item active");
        else
            l.setAttribute("class","page-item");

        let a = document.createElement("a");
        a.setAttribute("class","page-link");
        let pageNo = page
        if (!isNaN(+page)) {
            a.onclick = ()=>{changePage(+page)}
            pageNo = (+page + 1).toString()
        }
        a.innerHTML = pageNo;
        l.appendChild(a);
        pagespan.appendChild(l);
    })
}

function printingTable(doctors,tbody){

    //create all element
    const tr = document.createElement("tr");
    const firstName = document.createElement("td");
    const lastName = document.createElement("td");
    const email = document.createElement("td");
    const mobile = document.createElement("td");
    const photoId = document.createElement("td");
    const photoImg = document.createElement("img");
    const rating = document.createElement("td");
    const action = document.createElement("td");
    const viewLink = document.createElement("a");
    const viewIcon = document.createElement("i");
    const appointmentLink = document.createElement("a");
    const appointmentIcon = document.createElement("i");
    const chatLink = document.createElement("a");
    const chatIcon = document.createElement("i");

    //setting values and appending it
    firstName.innerHTML = doctors.firstName;
    tr.appendChild(firstName);

    lastName.innerHTML = doctors.lastName;
    tr.appendChild(lastName);

    email.innerHTML = doctors.email;
    tr.appendChild(email);

    mobile.innerHTML = doctors.mobileNumber;
    tr.appendChild(mobile);

    //sending id to get profile pictures
    photoImg.setAttribute("src","/display/image/?id="+doctors.id);
    photoImg.setAttribute("class","rounded-circle");
    photoImg.setAttribute("height","100px");
    photoImg.setAttribute("width","100px");
    photoId.appendChild(photoImg);
    tr.appendChild(photoId);

    //if rating is available
    if(doctors.avgRating){
        rating.innerHTML = ''+ parseFloat(doctors.avgRating).toFixed(1);
    }
    // if rating is not available
    else {
        rating.innerHTML = "0.0";
    }

    tr.appendChild(rating);

    //setting links
    viewLink.setAttribute("title","View Doctor");
    viewLink.setAttribute("class","btn btn-info");
    viewLink.setAttribute("style","margin:0px 1px");
    viewLink.setAttribute("href","http://localhost:8080/patient/doctor/view/?id="+ doctors.id);
    viewIcon.setAttribute("class","fa fa-eye");
    viewLink.appendChild(viewIcon);
    action.appendChild(viewLink);

    appointmentLink.setAttribute("title","Book Appointment");
    appointmentLink.setAttribute("class","btn btn-success");
    appointmentLink.setAttribute("style","margin:0px 2.5px");
    appointmentLink.setAttribute("href","/appointment/book/?id="+ doctors.id);
    appointmentIcon.setAttribute("class","fas fa-calendar-check");
    appointmentLink.appendChild(appointmentIcon);
    action.appendChild(appointmentLink);

    chatLink.setAttribute("class","btn btn-success");
    chatLink.setAttribute("href","/patient/chat/?doctorId="+ doctors.id);
    chatIcon.setAttribute("class","far fa-comment");
    chatLink.appendChild(chatIcon);
    action.appendChild(chatLink);

    tr.appendChild(action);

    tbody.appendChild(tr);

}

 const search = (page)=>{

        //set current page number
        currentPage = page;
        let rowPerPage = document.getElementById("row-per-page").value;

        //get pagination div
        let paginationDiv = document.getElementById("paginationDiv");

        //get keywords typed from search box
        let keywords = $("#search-input").val().toLowerCase();

        //get body element and empty the body content
        const tbody  = document.getElementById("dataTable");
        tbody.innerHTML = '';

        //sending page number and no. of row per page to the controller
        let url = '/search/';

         const formData = new FormData();
         formData.append('keywords',keywords);
         formData.append('page',page);
         formData.append('size',rowPerPage);

     fetch(url,{
         method: 'POST',
         body: formData
     })
        .then((response) => {
        return response.json();
        })
        .then((data) => {

            //if no data is returned
            if(data.content.length <= 0){

                console.log("in if")

                paginationDiv.style.display = "none";

                tbody.innerHTML = '';

                const msgTR = document.createElement("tr");
                const msgTD = document.createElement("td")

                //setting message that no data has been found
                msgTD.setAttribute("colspan","7")
                msgTD.innerHTML = "No Doctor found"
                msgTD.style = "text-align: center; font-size:40px";
                msgTR.appendChild(msgTD)
                tbody.appendChild(msgTR);
            }
            //if data is returned
            else {

                paginationDiv.style.display = "block";

                totalPages = data.totalPages-1

                for (let i=0;i<data.content.length;i++){
                    //printing onw row at a time
                    printingTable(data.content[i],tbody)
                }

                //getting the pagination
                let pagespan = document.getElementById("page-number-span")
                pagespan.innerHTML = '';

                if(rowPerPage >= data.totalElements)
                    getPageNumbers(totalPages,pagespan,true)
                else
                    getPageNumbers(totalPages,pagespan,false)

            }
        });
}

function onFirst(){
    search(0)
}

function onLast(){
    search(totalPages)
}

function onNext(){

    if((currentPage+1) <= totalPages){
        currentPage++;
        search(currentPage)
    }
}

function onPrevious(){

    if((currentPage-1) >= 0){
        search(currentPage-1)
    }
}

function changePage(pageNumber){
    currentPage = pageNumber
    search(currentPage)
}