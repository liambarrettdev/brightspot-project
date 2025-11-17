import $ from 'jquery'
import bsp_utils from 'bsp-utils'

export class ListModule {

    constructor(el) {
        this.$el = $(el)
        this.$itemsContainer = this.$el.find('[data-list-items]')
        this.$paginationContainer = this.$el.find('[data-list-pagination]')
        
        // Extract base URL from the first pagination link or use current URL
        const $firstLink = this.$el.find('.Pagination a[href]').first()
        if ($firstLink.length) {
            const href = $firstLink.attr('href')
            if (href) {
                // Remove the page parameter to get base URL
                const url = new URL(href, window.location.origin)
                url.searchParams.delete('p')
                this.baseUrl = url.pathname + url.search
            } else {
                this.baseUrl = window.location.pathname + window.location.search
            }
        } else {
            this.baseUrl = window.location.pathname + window.location.search
        }
        
        this.init()
    }

    init() {
        // Intercept clicks on pagination links
        this.$el.on('click', '.Pagination a[href]', (e) => {
            e.preventDefault()
            const href = $(e.currentTarget).attr('href')
            if (href && href !== '#') {
                this.loadPage(href)
            }
        })
        
        // Handle browser back/forward buttons
        // Only handle if URL contains pagination parameter
        const handlePopState = (e) => {
            const urlParams = new URLSearchParams(window.location.search)
            if (urlParams.has('p') || e.originalEvent.state) {
                this.loadPage(window.location.href)
            }
        }
        
        // Store handler reference for cleanup if needed
        this.popStateHandler = handlePopState
        $(window).on('popstate', handlePopState)
    }

    loadPage(url) {
        // Show loading state
        this.$itemsContainer.addClass('loading')
        
        // Make AJAX request
        $.ajax({
            url: url,
            method: 'GET',
            dataType: 'html',
            success: (html) => {
                this.updateContent(html, url)
            },
            error: (xhr, status, error) => {
                console.error('Error loading page:', error)
                this.$itemsContainer.removeClass('loading')
                // Fallback to regular navigation
                window.location.href = url
            }
        })
    }

    updateContent(html, url) {
        // Parse the HTML response - it will be the full page HTML
        const $response = $(html)
        
        // Find the ListModule in the response using data attribute
        const $responseModule = $response.find('[data-list-module]')
        
        // If we found the module in the response, extract its content
        if ($responseModule.length) {
            const $newItemsContainer = $responseModule.find('[data-list-items]')
            const $newPaginationContainer = $responseModule.find('[data-list-pagination]')
            
            if ($newItemsContainer.length) {
                // Update items with fade effect
                this.$itemsContainer.fadeOut(200, () => {
                    this.$itemsContainer.html($newItemsContainer.html())
                    this.$itemsContainer.removeClass('loading')
                    this.$itemsContainer.fadeIn(200)
                })
            } else {
                this.$itemsContainer.removeClass('loading')
            }
            
            if ($newPaginationContainer.length) {
                // Update pagination
                this.$paginationContainer.html($newPaginationContainer.html())
            }
        } else {
            // Fallback: try to find by data attributes directly
            const $newItemsContainer = $response.find('[data-list-items]')
            const $newPaginationContainer = $response.find('[data-list-pagination]')
            
            if ($newItemsContainer.length) {
                this.$itemsContainer.fadeOut(200, () => {
                    this.$itemsContainer.html($newItemsContainer.first().html())
                    this.$itemsContainer.removeClass('loading')
                    this.$itemsContainer.fadeIn(200)
                })
            } else {
                this.$itemsContainer.removeClass('loading')
            }
            
            if ($newPaginationContainer.length) {
                this.$paginationContainer.html($newPaginationContainer.first().html())
            }
        }
        
        // Update browser URL without reload
        if (window.history && window.history.pushState) {
            window.history.pushState({ url: url }, '', url)
        }
        
        // Scroll to top of the list module
        const offset = this.$el.offset()
        if (offset) {
            $('html, body').animate({
                scrollTop: offset.top - 20
            }, 300)
        }
    }
}

export default bsp_utils.plugin(false, 'bsp', 'promo-list-module', {
    '_each': function (item) {
        new ListModule($(item))
    }
});

