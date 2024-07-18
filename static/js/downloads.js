async function get_all_files() {
    var output = null;
    try {
        response = await fetch("https://coreform.com/download-api/listdownloads");
        if (response.status >= 400) {
            console.warn(response);
            if (response.status == 500) {
                console.warn(response.json());
            }
        }
        var files = await response.json();
        if(!files.hasOwnProperty("downloads")) {
            output = {
                "downloads": {
                    "Coreform-Cubit": files,
                }
            }
        } else {
            output = files
        }
        console.log(output);
    } catch (error) {
        console.warn(error);
    }
    return output;
}
