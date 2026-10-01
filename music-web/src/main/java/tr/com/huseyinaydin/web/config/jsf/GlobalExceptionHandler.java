package tr.com.huseyinaydin.web.config.jsf;

import jakarta.faces.FacesException;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.sharedkernel.exception.NotFoundException;

import java.util.Iterator;

public class GlobalExceptionHandler extends ExceptionHandlerWrapper {

    public GlobalExceptionHandler(ExceptionHandler wrapped) {
        super(wrapped);
    }

    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            Throwable t = context.getException();
            
            FacesContext facesContext = FacesContext.getCurrentInstance();
            NavigationHandler nav = facesContext.getApplication().getNavigationHandler();
            
            try {
                // Hata tipini belirle ve ilgili sayfaya yönlendir
                Throwable rootCause = getRootCause(t);
                System.err.println("[JSF HATA YAKALAYICI] " + rootCause.getMessage());

                if (rootCause instanceof AuthorizationException) {
                    nav.handleNavigation(facesContext, null, "/error/403.xhtml?faces-redirect=true");
                } else if (rootCause instanceof NotFoundException) {
                    nav.handleNavigation(facesContext, null, "/error/404.xhtml?faces-redirect=true");
                } else if (rootCause instanceof BusinessException) {
                    // İş kuralı hatası ise kullanıcıya mesaj göstermek için aynı sayfada kalıp uyarı verilebilir,
                    // ancak burada global 500'e (veya özel sayfaya) yönlendiriyoruz.
                    facesContext.getExternalContext().getFlash().put("errorMessage", rootCause.getMessage());
                    nav.handleNavigation(facesContext, null, "/error/500.xhtml?faces-redirect=true");
                } else {
                    // Beklenmeyen sistem hataları
                    nav.handleNavigation(facesContext, null, "/error/500.xhtml?faces-redirect=true");
                }
                
                facesContext.renderResponse();
            } finally {
                i.remove();
            }
        }
        
        getWrapped().handle();
    }

    private Throwable getRootCause(Throwable t) {
        Throwable cause = t.getCause();
        while (cause != null && cause != t) {
            t = cause;
            cause = t.getCause();
        }
        return t;
    }
}
