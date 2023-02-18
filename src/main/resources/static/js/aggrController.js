adxcelApp.controller("aggrController", function ($scope, $resource, $animate) {

    $scope.REGISTRATION = 'registration';
    $scope.SIGNUP = 'signup';
    $scope.MISC = 'misc';
    $scope.LEAD = 'lead';
    $scope.CONTENT = 'content';

    $scope.DMA = 'dma';
    $scope.SITE = 'site';

    // sort
    $scope.CTR = 'ctr';
    $scope.EVPM = 'evpm';
    $scope.GROUP = 'group';
    $scope.IMPR = 'impr';

    $scope.ASC = 'asc';
    $scope.DESC = 'desc';

    $scope.rowsQtyList = [25, 50, 100];

    $scope.currDmaEvent;
    $scope.currSiteEvent;



    $scope.isAggrDmaProgress = false;
    $scope.isAggrSiteProgress = false;

    $animate.enabled(false);

    $scope.scrollToMe = function (event) {
        event.target.scrollIntoView();
    }



    class AggrTable {
        map;
        table;
        filter = []; // if empty, then show all (TODO actually not used in UI)
        sortField = $scope.GROUP;
        sortOrder = $scope.ASC;
        rowsOnPage = 25;

        // optimization implemented for site table due to large data volume
        proxy;
        selectedIndex = 0;

        constructor() {
        }

        constructTable(resp) {
            this.proxy = [];
            this.table = [];
            this.selectedIndex = 0;
            if (resp && Array.isArray(resp)) {
                this.map = new Map();
                resp.forEach((item) => this.map.set(item.group, item));
            } else if (!this.map) {
                console.error('Cannot find preprocessed map object at construct table');
                return;
            }
            let table = this.filterMap();
            this.roundValues(table)
            this.sortTable(table);
            this.table = this.divideArray(table, this.rowsOnPage);
            this.proxy = [];
            for (let i = 0; i < this.table.length; i++) {
                this.proxy.push(i); // unique content needed for ng-repeat directive
            }
            this.fillProxy(this.selectedIndex);
        }

        // Used for site table only
        fillProxy(selectedIndex, doFocus) {
            if (this.proxy[selectedIndex] == undefined) {
                selectedIndex = this.proxy.length - 1;
            }
            this.selectedIndex = selectedIndex
            this.proxy[selectedIndex] = this.table[selectedIndex];
            if (doFocus) {
                setTimeout(() => {
                    console.log('focusing');
                    document.getElementById("footer").scrollIntoView();}, 2000);
            }
        }

        // TODO filter is not implemented at UI
        updateFilter(arr) {
            this.filter = arr;
            this.constructTable();
        }


        switchSort(group) {
            if (group == this.sortField) {
                this.sortOrder = this.sortOrder == $scope.ASC ? $scope.DESC : $scope.ASC;
            } else {
                this.sortField = group;
                this.sortOrder = $scope.ASC;
            }
            this.constructTable();
        }

        updatePages() {
            this.rowsOnPage = parseInt(this.rowsOnPage);
            this.constructTable();
        }

        filterMap() {
            if (this.filter.length == 0) {
                return Array.from(this.map.values());
            } else {
                let arr = [];
                this.filter.forEach((item) => arr.push(this.map.get(item)));
                return arr;
            }
        }

        roundValues(arr) {
            arr.forEach((item) => {
                item.ctr = Math.round(item.ctr * 100) / 100;
                item.evpm = Math.round(item.evpm * 100) / 100;
            });
        }

        sortTable(arr) {
            arr.sort((a, b) => {
                const sign = this.sortOrder == $scope.ASC ? 1 : -1;
                if (a[this.sortField] < b[this.sortField]) {
                    return -1 * sign;
                }
                if (a[this.sortField] > b[this.sortField]) {
                    return 1 * sign;
                }
                return 0;
            });
        }

        divideArray(arr, rows) {
            if (!Array.isArray(arr) || rows == 0 || arr.length == 0) {
                return [];
            }
            let result = [];
            for (let i = 0; i < arr.length; i += rows) {
                result.push((arr.slice(i, i + rows)));
            }
            return result;
        }
    }

    $scope.aggrSiteObj = new AggrTable();
    $scope.aggrDmaObj = new AggrTable();

    let actions = {
        get: {
            method: 'get',
            isArray: true,
            headers: {},
        }
    };

    $scope.onGetAggr = function (group, event) {
        switch (group) {
            case $scope.DMA:
                $scope.isAggrDmaProgress = true;
                break;
            case $scope.SITE:
                $scope.isAggrSiteProgress = true;
                break;
        }
        let Rate = $resource(`${BASE_URL}api/aggr/${group}/${event}`, {}, actions);
        Rate.get().$promise.then((resp) => {
                validate('aggrSchema', resp);
                switch (group) {
                    case $scope.DMA:
                        $scope.currDmaEvent = event;
                        $scope.aggrDmaObj.constructTable(resp);
                        $scope.isAggrDmaProgress = false;
                        break;
                    case $scope.SITE:
                        $scope.aggrSiteObj.constructTable(resp);
                        $scope.currSiteEvent = event;
                        $scope.isAggrSiteProgress = false;
                        break;
                }
            },
            (rej) => {
                console.error(JSON.stringify(rej));
            });
    }

    $scope.onGetAggr($scope.DMA, $scope.REGISTRATION);
    $scope.onGetAggr($scope.SITE, $scope.REGISTRATION);


});