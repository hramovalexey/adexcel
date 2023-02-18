adxcelApp.controller("rateController", function ($scope, $resource, $mdDialog) {
    $scope.DAY = 'day';
    $scope.HOUR = 'hour';
    $scope.MINUTE30 = 'minute30';

    $scope.REGISTRATION = 'registration';
    $scope.SIGNUP = 'signup';
    $scope.MISC = 'misc';
    $scope.LEAD = 'lead';
    $scope.CONTENT = 'content';

    $scope.currCtrTime;
    $scope.currEvpmTime;
    $scope.currEvpmEvent;

    $scope.isRateCtrProgress = false;
    $scope.isRateEvpmProgress = false;

    let currentCtrGraph;
    let currentEvpmGraph

    let actions = {
        get: {
            method: 'get',
            isArray: true,
            headers: {
            },
        }
    };

    $scope.onGetEvpm = function (event, timeResolution) {
        $scope.isRateEvpmProgress = true;
        let Rate = $resource(`${BASE_URL}api/evpm/${event}/${timeResolution}`, {}, actions);
        Rate.get().$promise.then((resp) => {
                validate('rateSchema', resp);
                parseRateDate(resp);
                const graphOptions = {
                    title: `EvPM of ${event}`,
                    data: generateGraphData(resp),
                    axisYName: `${event} EvPM, \u2030`,
                    axisXName: timeResolutionMap.get(timeResolution),
                    dateStrCallback: timeResolution == 'day' ? getDayStr : getHourStr,
                    elementId: 'evpmChart',
                    lineColor: '#dd6685'
                }
                if (currentEvpmGraph) {
                    currentEvpmGraph.destroy();
                }
                currentEvpmGraph = drawGraph(graphOptions);
                $scope.currEvpmTime = timeResolution;
                $scope.currEvpmEvent = event;
                $scope.isRateEvpmProgress = false;
            },
            (rej) => {
                console.error(JSON.stringify(rej));
            });
    }

    $scope.onGetCtr = function (timeResolution) {
        $scope.isRateCtrProgress = true;
        let Rate = $resource(`${BASE_URL}api/ctr/${timeResolution}`, {}, actions);
        Rate.get().$promise.then((resp) => {
                validate('rateSchema', resp);
                parseRateDate(resp);
                const graphOptions = {
                    title: `CTR`,
                    data: generateGraphData(resp),
                    axisYName: `CTR, %`,
                    axisXName: timeResolutionMap.get(timeResolution),
                    dateStrCallback: timeResolution == 'day' ? getDayStr : getHourStr,
                    elementId: 'ctrChart',
                    lineColor: '#6c99e5'
                }
                if (currentCtrGraph) {
                    currentCtrGraph.destroy();
                }
                currentCtrGraph = drawGraph(graphOptions);
                $scope.currCtrTime = timeResolution;
                $scope.isRateCtrProgress = false;
            },
            (rej) => {
                console.error(JSON.stringify(rej));
            });
    }

    function parseRateDate(obj) {
        if (!Array.isArray(obj)) {
            throw('Invalid array');
        }
        for (let item of obj) {
            item.time = new Date(Date.parse(item.time));
        }
    }

    function parseRateDay(obj) {
        if (!Array.isArray(obj)) {
            throw('Invalid array');
        }
        for (let item of obj) {
            item.time = item.time.substring(0, item.time.indexOf('T'));
        }
    }

    // Format as 05.05.2012
    function getDayStr(date) {
        if (date.constructor.name !== 'Date') {
            throw('Invalid date format');
        }
        let str = date.toLocaleString();
        return str.substring(0, str.indexOf(','));
    }

    // Format as 05.05.2012, 07:00
    function getHourStr(date) {
        if (date.constructor.name !== 'Date') {
            throw('Invalid date format');
        }
        let str = date.toLocaleString();
        return str.substring(0, str.length - 3);
    }

    function generateGraphData(rateObj) {
        let result = [];
        for (let item of rateObj) {
            result.push({x: item.time, y: item.rate, imprq: item.imprq});
        }
        return result;
    }

    function drawGraph(opt) {
        const ctx = document.getElementById(opt.elementId);
        const data = {
            datasets: [{
                label: opt.title,
                data: opt.data,
                borderWidth: 1,
            }]
        }

        const config = {
            type: 'line',
            data: data,
            options: {
                elements: {
                    point: {
                        radius: 2,
                        hoverRadius: 4,
                        hitRadius: 10
                    },
                    line: {
                        borderColor: opt.lineColor
                    }
                },
                responsive: true,
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: opt.axisXName
                        },
                        type: 'time',
                        ticks: {
                            autoSkip: true,
                            maxTicksLimit: 5
                        },
                        time: {
                            unit: 'day',
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: opt.axisYName
                        },
                        beginAtZero: true
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            title: (context) => {
                                let date = new Date(context[0].parsed.x);
                                return opt.dateStrCallback(date);
                            },
                            label: (context) => {
                                return '' + (Math.round(context.raw.y * 100) / 100) + ' \u2030';
                            },
                            afterLabel: (context) => {
                                return "Impressions: " + context.raw.imprq;
                            }

                        }
                    }
                }
            }
        }
        return new Chart(ctx, config);
    }

    $scope.onGetCtr($scope.DAY);
    $scope.onGetEvpm($scope.REGISTRATION, $scope.DAY);
});