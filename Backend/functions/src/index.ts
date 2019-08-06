const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

function formatTwoDigits(value){
    if(value < 10){
        return "0" + value;
    }else{
        return value;
    }
}

exports.insertDictionaries = functions.storage.object().onFinalize(async (object)  => {  
    const filePath = object.name;
    if(filePath.startsWith("toUpload") && filePath.endsWith("json")){
        console.log(filePath);
        await admin.storage().bucket().file(filePath)
        .download().then((contents) => {
            let finishedOk = true;
            const date = new Date;
            let time = parseInt("" + date.getUTCFullYear() 
            + formatTwoDigits((date.getUTCMonth() + 1))
            + formatTwoDigits(date.getUTCDate())
            + formatTwoDigits(date.getUTCHours())
            + formatTwoDigits(date.getUTCMinutes()));
            (async () => {
                try{
                    const data = JSON.parse(contents.toString('utf8'));         
                    const db = admin.database();
                    const ref = db.ref("dictionary");

                    for (let i = 0; i < data.dictionary.length && finishedOk; i++){
                        if(i % 100 == 0){
                            console.log(i + " / " + data.dictionary.length + " inserted");
                        }
                        const obj = data.dictionary[i];                    

                        await ref.child(obj.language+"_"+obj.word)
                        .set({
                            word: obj.word,
                            language: obj.language,
                            createdAt: time
                        }).then(() => {
                            finishedOk = true;   
                            //console.log("Document written");
                        }).catch(function(error) {
                            finishedOk = false;                   
                            console.error("Error adding document: ", error);
                        });
                    };
                    if(finishedOk){
                        await admin.storage().bucket().file(filePath)
                        .move(filePath.replace("toUpload/", "done/"), function(err, destinationFile, apiResponse) {
                            if(!err){
                                console.log(filePath + " moved correctly");
                            }else{
                                console.error("Error moving "+ filePath, err);
                            }
                        });         
                        //TODO add send notification         
                    }else{
                        console.error("Error during insertion. ReUpload the file " + filePath);
                    }
                }catch(error){
                    finishedOk = false;   
                    console.error("error en catch" , error);
                }
            })().catch(e => console.log("Caught: " + e));
        });
    }    
});