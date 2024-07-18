(function() {

    var tab = document.querySelectorAll('button[data-tabset]');
    if (tab != null) {

        var i, el, tabcontent, tabset, tabSetList, tabContentList;

        var clear = function(nodeList) {
            for (i = 0; i < nodeList.length; i++) {
                nodeList[i].classList.remove('active');
            }
        }

        var onTabClick = function() {
            tabset = event.target.dataset.tabset;
            tabcontent = event.target.dataset.tabcontent;
            tabSetList = document.querySelectorAll('button[data-tabset="'+ tabset +'"]');
            tabContentList = document.querySelectorAll('.tabcontent[data-tabset="'+ tabset +'"]');
            clear(tabSetList);
            event.target.classList.add('active');
            clear(tabContentList);
            el = document.querySelector('.tabcontent[data-tabset="' + tabset + '"].tabcontent[data-tabcontent="' + tabcontent + '"]');
            if (el != null) {
                el.classList.add('active');
            }
        }

        for (i = 0; i < tab.length; i++) {
            tabset = tab[i].dataset.tabset;
            tabcontent = tab[i].dataset.tabcontent;

            // add `tabs` class to parent element
            if (!tab[i].parentElement.classList.contains('tabs')) {
                tab[i].parentElement.classList.add('tabs');
            }

            // show active content
            if (tab[i].classList.contains('active')) {
                el = document.querySelector('.tabcontent[data-tabset="' + tabset + '"].tabcontent[data-tabcontent="' + tabcontent + '"]');
                if (el != null) {
                    el.classList.add('active');
                }
            }
        }

        for (i = 0; i < tab.length; i++) {
            tab[i].addEventListener('click', onTabClick);
        }
    }

})();