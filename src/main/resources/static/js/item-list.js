console.log("Hello")
const table = document.getElementById("table");
const inventory = document.getElementById("inventory");

let tempList = [];
let dbList = [];

for (let i = 1, row; row = table.rows[0];i ++){
    tempList.push(row.cells[1].innerText)
}
for (let j = 0; j < inventory.length; j++){
    // let opt = document.createElement("option")
    // opt.innerText = tempList
    dbList.push(inventory.options[j].value)
}

dbList = dbList.filter(val => !tempList.includes(val));

inventory.innerHTML = ""
for (let j = 0; j < dbList.length; j++){
    let opt = document.createElement("option")
    opt.value = dbList[j]
    opt.innerText = dbList[j]
    inventory.appendChild(opt)
}