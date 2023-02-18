const aggrSchema = {
    "type": "array",
    "items": {
        "type": "object",
        "properties": {
            "ctr": {
                "type": "number"
            },
            "evpm": {
                "type": "number"
            },
            "group": {
                "type": ["number", "string"]
            },
            "impr": {
                "type": "number"
            }
        },
        "required": [
            "ctr",
            "evpm",
            "group",
            "impr"
        ]
    }
}

const rateSchema = {
    "type": "array",
    "items": {
        "type": "object",
        "properties": {
            "time": {
                "type": "string"
            },
            "rate": {
                "type": "number"
            }
            ,
            "imprq": {
                "type": "number"
            }
        },
        "required": [
            "time",
            "rate",
            "imprq"
        ]
    }
}

const timeResolutionMap = new Map([
    ['day', 'Days'],
    ['hour', 'Hours'],
    ['minute30', '30 minutes']]
);

const validator = new djv();
validator.addSchema('aggrSchema', aggrSchema);
validator.addSchema('rateSchema', rateSchema);

function validate(schemaName, object) {
    let isNotValid = validator.validate(`${schemaName}#/common`, object);
    if (isNotValid) {
        throw("Validation error: = " + JSON.stringify(isNotValid));
    }
}
const BASE_URL = location.protocol + "//"+ location.host + "/";

let adxcelApp = angular.module("adxcelApp", ['ngResource', 'ngMaterial', 'ngMessages'])
    .config(function ($mdThemingProvider) {
        $mdThemingProvider.theme('default')
            .primaryPalette('grey')
// .accentPalette('orange');
    });


