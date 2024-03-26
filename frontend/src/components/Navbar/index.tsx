import { Link } from "react-router-dom";
import { CgMenu } from "react-icons/cg";
import { IoCloseSharp } from "react-icons/io5";
import { useState } from "react";
import { useMediaQuery } from "react-responsive";
import SignupButton from "../SignupButton";
import NavLink from "../NavLink";

function Navbar() {
  const flexBetween = "flex items-center justify-between";
  const isAboveMediumScreens = useMediaQuery({ query: "(min-width: 1060px)" });
  const [isMenuToggled, setIsMenuToggled] = useState<boolean>(false);

  return (
    <nav>
      <div className={`${flexBetween} w-full py-6 `}>
        <div className={`${flexBetween} mx-auto w-5/6 `}>
          <div className={`${flexBetween} w-full `}>
            {/* LEFT SIDE */}
            <p className="ubuntu-medium ">Bookshelf Boulevard </p>

            {/* RIGHT SIDE */}
            {isAboveMediumScreens ? (
              <div className={`${flexBetween}  gap-16  `}>
                <NavLink to="/about" text="About" />
                <NavLink to="/login" text="Login" />
                <Link to={"/signup"} className="m-3">
                  <SignupButton />
                </Link>
              </div>
            ) : (
              <div className="flex items-center gap-6 ">
                <Link to={"/signup"} className="m-3">
                  <SignupButton />
                </Link>

                <button onClick={() => setIsMenuToggled(!isMenuToggled)}>
                  <CgMenu className="text-3xl text-black cursor-pointer"></CgMenu>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* MOBILE MENU MODAL */}
      {!isAboveMediumScreens && isMenuToggled && (
        <div className="fixed right-0 bottom-0 z-40 h-full w-[300px] bg-slate-50 drop-shadow-xl">
          {/* CLOSE ICON */}
          <div className="flex justify-end p-12">
            <button onClick={() => setIsMenuToggled(!isMenuToggled)}>
              <IoCloseSharp className="text-3xl text-black cursor-pointer"></IoCloseSharp>
            </button>
          </div>
          {/* MENU ITEMS */}
          <div className="ml-[33%] flex flex-col gap-10 text-2xl">
            <NavLink to="/about" text="About" />
            <NavLink to="/login" text="Login" />
          </div>
        </div>
      )}
    </nav>
  );
}

export default Navbar;
