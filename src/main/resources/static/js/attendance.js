var currentDate = new Date();
var currentMonthNumber = currentDate.getMonth()+1;
var currentYear = currentDate.getFullYear();
var currentYearMonth = currentYear*100 + currentMonthNumber;

const monthDays = {
    January : 31,
    February : 28,
    March : 31,
    April : 30,
    May : 31,
    June : 30,
    July : 31,
    August : 31,
    September : 30,
    October: 31,
    November : 30,
    December : 31
};

var years = []
var startYear=2018;
var i = 0;

//getting years till current year
while(startYear<=currentYear){
    years[i] =startYear;
    startYear++;
    i++;
}

const monthNumber = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"];

// program to check leap year
function checkLeapYear(year) {

    const leap = new Date(year, 1, 29).getDate() === 29;
    if (leap) {
        monthDays.February = 29;
    } else {
        monthDays.February = 28;
    }
}


const getAllMonthAndYear=()=>{

    //get all months
    const selectMonth = document.getElementById("select-month-id");
    selectMonth.innerHTML = "";

    for (let monthDaysKey in monthDays) {
        const option = document.createElement("option");
        if(monthDaysKey == monthNumber[currentDate.getMonth()]){
            option.selected = true;
        }
        option.setAttribute("value",monthDaysKey);
        option.innerHTML = monthDaysKey;
        selectMonth.appendChild(option);

    }

    //get all year
    const selectYear = document.getElementById("select-year-id")
    selectYear.innerHTML = "";

    for (let i = 0; i < years.length; i++) {
        const option = document.createElement("option");
        if(years[i] == currentDate.getFullYear()){
            option.selected = true;
        }
        option.setAttribute("value",years[i]);
        option.innerHTML = years[i];
        selectYear.appendChild(option);

    }

}

const getData=()=> {

    //get selected month and year
    const selectedMonth = document.getElementById("select-month-id").value;
    const selectedYear = document.getElementById("select-year-id").value;

    checkLeapYear(selectedYear);

    const trMonth = document.getElementById("days-per-month-th");
    trMonth.innerHTML = "";

    let days = monthDays[selectedMonth];

    const th = document.createElement("th");
    th.innerHTML = "Name";
    trMonth.appendChild(th);

    //append 0 for single digit value
    let month = ('0'+(monthNumber.indexOf(selectedMonth)+1)).slice(-2);

    let currentDate = new Date();


    for (let i = 1; i <= days; i++) {

        if((month == currentDate.getMonth()+1) && (selectedYear == currentDate.getFullYear()) && (i > currentDate.getDate())){
            break;
        }

        const th = document.createElement("th");
        th.innerHTML = i;
        trMonth.appendChild(th);
    }

    var checkYearMonth = selectedYear + month;

    //if selected year and month is less than current month and year
    if (checkYearMonth<= currentYearMonth){

        let url = 'http://localhost:8080/attendance/?day='+days+"&month="+month+"&year="+selectedYear;

        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {

                tbody.innerHTML = '';

                for (const dataKey in data) {

                    let tr = document.createElement("tr");
                    printAttendance(tr,data[dataKey],days,month,selectedYear,currentDate)
                    tbody.appendChild(tr);

                }
            });

    }
    else {
        tbody.innerHTML = '';
    }


}

const printAttendance=(tr,member,days,month,year,currentDate)=>{


    let memberName = document.createElement("td");
    memberName.innerHTML = member.name;
    tr.appendChild(memberName);

    let joined = new Date(member.joiningDate)

    for (let i = 1; i <= days; i++) {

        //if days are greater than current day
        if((month == currentDate.getMonth()+1) && (year == currentDate.getFullYear()) && (i > currentDate.getDate())){
            break;
        }

        td = document.createElement("td");

        //if date is less than joined date
        if((joined.getFullYear() == year) && ((joined.getMonth()+1) == month) && (i < joined.getDate())){

            td.setAttribute("style","font-weight:bold");
            td.innerHTML = '-';

        } else {

            for (const day in member.absentList) {

                //if member is absent
                if(i == member.absentList[day]){
                    td.setAttribute("style","color:red; font-weight:bold");
                    td.innerHTML = 'A';
                    break;
                }
                //if member is present
                else {
                    td.setAttribute("style","color:green; font-weight:bold");
                    td.innerHTML = 'P';
                }
            }
        }

        tr.appendChild(td);
    }
}
