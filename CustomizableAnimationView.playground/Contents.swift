//: A UIKit based Playground for presenting user interface
  
import UIKit
import PlaygroundSupport

public class CustomizableAnimationView: UIView {
    
    enum Shape {
        case checkmark
        
        func path(bounds: CGRect) -> CGPath {
            switch self {
            case .checkmark:
                let path = UIBezierPath()
                path.addArc(
                    withCenter: CGPoint(
                        x: bounds.midX,
                        y: bounds.midY
                    ),
                    radius: bounds.midX,
                    startAngle: .pi / 2,
                    endAngle: .pi * 2.5,
                    clockwise: true
                )
                path.move(
                    to: CGPoint(
                        x: bounds.width / 4,
                        y: bounds.width / 2
                    )
                )
                path.addLine(
                    to: CGPoint(
                        x: bounds.width * 0.45,
                        y: bounds.width * 0.75
                    )
                )
                path.addLine(
                    to: CGPoint(
                        x: bounds.width * 0.75,
                        y: bounds.width * 0.25
                    )
                )
                return path.cgPath
            }
        }
    }
    
    private let shape: Shape
    
    private let animatedLayer = CAShapeLayer()
    
    init(shape: Shape) {
        self.shape = shape
        super.init(frame: CGRect(x: 0, y: 0, width: 200, height: 200))
        
        layer.addSublayer(animatedLayer)
        configureAnimatedLayer()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    public override func layoutSubviews() {
        super.layoutSubviews()
        updatePath()
    }
    
    private func configureAnimatedLayer() {
        animatedLayer.fillColor = UIColor.white.cgColor
        animatedLayer.strokeColor = UIColor.green.cgColor
        animatedLayer.lineWidth = 6
    }
    
    private func updatePath() {
        animatedLayer.path = shape.path(bounds: bounds)
    }
    
    func startAnimation() {
        stopAnimation()
        
        animatedLayer.strokeEnd = 1
        let anim = CABasicAnimation(keyPath: "strokeEnd")
        anim.duration = 1
        anim.fromValue = 0
        anim.toValue = 1
        animatedLayer.add(anim, forKey: "path")
    }
    
    func stopAnimation() {
        animatedLayer.removeAllAnimations()
    }
    
}

class MyViewController : UIViewController {
    private let animationView = CustomizableAnimationView(shape: .checkmark)
    override func loadView() {
        let view = UIView()
        view.backgroundColor = .white

//        let label = UILabel()
//        label.frame = CGRect(x: 150, y: 200, width: 200, height: 20)
//        label.text = "Hello World!"
//        label.textColor = .black
        animationView.frame = CGRect(x: 100, y: 200, width: 200, height: 200)
        
        view.addSubview(animationView)
        
        self.view = view
        self.view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(startAnim)))
        self.view.isUserInteractionEnabled = true
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        animationView.startAnimation()
    }
    
    @objc
    func startAnim() {
        animationView.startAnimation()
    }
    
}
// Present the view controller in the Live View window
PlaygroundPage.current.liveView = MyViewController()
