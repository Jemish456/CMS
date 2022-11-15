console.log("Hello")
const table = document.getElementsByTagName("table")[0];
const drugList = document.getElementById("drugList");

let tempList = [];
let dbList = [];

for (let i = 1, row; row = table.rows[i];i ++){
    tempList.push(row.cells[1].innerText)
}
for (let j = 0; j < drugList.length; j++){
    // let opt = document.createElement("option")
    // opt.innerText = tempList
    dbList.push(drugList.options[j].value)
}
dbList = dbList.filter(val => !tempList.includes(val));

drugList.innerHTML = "";
for (let j = 0; j < dbList.length; j++){
    let opt = document.createElement("option")
    opt.value = dbList[j]
    opt.innerText = dbList[j]
    drugList.appendChild(opt)
}
